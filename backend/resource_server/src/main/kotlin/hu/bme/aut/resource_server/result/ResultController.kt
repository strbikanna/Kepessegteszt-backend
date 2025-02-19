package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommended_game.RecommenderService
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.utils.RoleName
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 * Controller for saving gameplay results and
 * querying previous game results
 */
@RestController
@RequestMapping("/gameplay")
class ResultController(
    @Autowired private var resultService: ResultService,
    @Autowired private var profileSnapshotService: ProfileSnapshotService,
    @Autowired private var authService: AuthService,
    @Autowired private var recommenderService: RecommenderService
) {

    /**
     * Endpoint to save results of a played game.
     * Returns the id of the next recommendation which has empty config initially.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveResult(@RequestBody gameplayData: ResultDto, authentication: Authentication): Long {
        authService.checkGameAccessAndThrow(authentication, gameplayData)
        val username = authentication.name
        if (!profileSnapshotService.existsSnapshotToday(username)) {
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        val savedResult = resultService.save(gameplayData)
        val game = resultService.getGameOfResult(savedResult.id!!)
        if (!game.active) {
            throw IllegalArgumentException("Game is not active");
        }
        var nextRecommendation =
            resultService.getNextRecommendationForGameIfExists(savedResult.recommendedGame.id!!, username)
        if (nextRecommendation != null && nextRecommendation.config.isNotEmpty()) {
            return nextRecommendation.id!!
        }
        if (nextRecommendation == null) {
            nextRecommendation = recommenderService.createEmptyRecommendation(username, game.id!!)
        }
        CoroutineScope(Dispatchers.Default).async {
            val config = recommenderService.createNextRecommendationByResult(savedResult)
            nextRecommendation.config = config
            recommenderService.save(nextRecommendation)
        }
        return nextRecommendation.id!!
    }

    @Transactional
    @GetMapping("/results")
    @ResponseStatus(HttpStatus.OK)
    fun getResultsByUser(
        authentication: Authentication,
        @RequestParam sortBy: String = "timestamp",
        @RequestParam sortOrder: String = "DESC",
        @RequestParam pageSize: Int = 100,
        @RequestParam pageIndex: Int = 0,
        @RequestParam gameIds: List<Int>? = null,
        @RequestParam resultWin: Boolean? = null
    ): List<ResultDetailsDto> {
        val username = authentication.name
        if (!gameIds.isNullOrEmpty() || resultWin != null) {
            return resultService.getAllFiltered(
                listOf(username),
                gameIds,
                resultWin,
                PageRequest.of(pageIndex, pageSize, resultService.convertSortBy(sortBy, sortOrder))
            )
        }
        return resultService.getAllByUser(
            username,
            PageRequest.of(pageIndex, pageSize, resultService.convertSortBy(sortBy, sortOrder))
        )
    }

    @Transactional
    @GetMapping("/results/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SCIENTIST', 'TEACHER', 'PARENT')")
    fun getAllResults(
        @RequestParam sortBy: String = "timestamp",
        @RequestParam sortOrder: String = "DESC",
        @RequestParam pageSize: Int = 100,
        @RequestParam pageIndex: Int = 0,
        @RequestParam gameIds: List<Int>? = null,
        @RequestParam resultWin: Boolean? = null,
        @RequestParam usernames: List<String>? = null,
        authentication: Authentication
    ): Deferred<List<ResultDetailsDto>> = CoroutineScope(Dispatchers.IO).async {
        val user = authService.getAuthUserWithRoles(authentication)
        val sort = resultService.convertSortBy(sortBy, sortOrder)
        if(user.roles.any { it.roleName == RoleName.ADMIN }){
            return@async if(usernames.isNullOrEmpty()){
                resultService.getAllFiltered(gameIds, resultWin, PageRequest.of(pageIndex, pageSize, sort))
            } else {
                resultService.getAllFiltered(usernames, gameIds, resultWin, PageRequest.of(pageIndex, pageSize, sort))
            }
        }
        val contactUsernames = authService.getContactUsernames(authentication)
        val usernamesToAccess =
            if (usernames.isNullOrEmpty()) contactUsernames else usernames.filter { contactUsernames.contains(it) }

        return@async resultService.getAllFiltered(
            usernamesToAccess,
            gameIds, resultWin, PageRequest.of(pageIndex, pageSize, sort)
        )
    }


    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun countResults(
        authentication: Authentication,
        @RequestParam gameIds: List<Int>? = null,
        @RequestParam resultWin: Boolean? = null,
        @RequestParam usernames: List<String>? = null
    ): Deferred<Long> = CoroutineScope(Dispatchers.IO).async {
        val user = authService.getAuthUserWithRoles(authentication)
        if(user.roles.none { Role.canGetContacts(it.roleName) }){
            return@async resultService.getCountByFilters(listOf(authentication.name), gameIds, resultWin)
        }
        val contactUsernames = authService.getContactUsernames(authentication)

        if(user.roles.any { it.roleName == RoleName.ADMIN }){
            return@async if(usernames.isNullOrEmpty()){
                resultService.getCountByFilters(gameIds, resultWin)
            } else {
                resultService.getCountByFilters(usernames, gameIds, resultWin)
            }
        }
        val usernamesToAccess =
            if (user.roles.any { Role.canSeeUserGroupData(it.roleName) || it.roleName == RoleName.PARENT }) {
                usernames?.filter { contactUsernames.contains(it) } ?: contactUsernames
            } else {
                listOf(authentication.name)
            }
        return@async resultService.getCountByFilters(usernamesToAccess, gameIds, resultWin)
    }

    @GetMapping("/csv")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    @PreAuthorize("hasRole('ADMIN') || hasRole('SCIENTIST')")
    fun exportResultsToCsv(response: HttpServletResponse) {
        response.contentType = "text/csv"
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"results.csv\"")
        val results = resultService.getAll()
        ExportCsvService.exportCsv(results, response.writer)
    }

}