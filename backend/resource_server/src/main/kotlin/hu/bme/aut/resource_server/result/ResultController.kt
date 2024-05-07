package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommended_game.RecommenderService
import jakarta.servlet.http.HttpServletResponse
import kotlinx.coroutines.CoroutineScope
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
        if(!profileSnapshotService.existsSnapshotToday(username)){
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        val savedResult = resultService.save(gameplayData)
        val game = resultService.getGameOfResult(savedResult.id!!)
        if(!game.active){
            throw IllegalArgumentException("Game is not active");
        }
        val existingNextRecommendation = resultService.getNextRecommendationForGameIfExists(savedResult.recommendedGame.id!!, username)
        if(existingNextRecommendation != null){
            return existingNextRecommendation.id!!
        }
        val nextRecommendation = recommenderService.createEmptyRecommendation(username, game.id!!)
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
        @RequestParam sortBy: String= "timestamp",
        @RequestParam sortOrder: String = "DESC",
        @RequestParam pageSize: Int = 0,
        @RequestParam pageIndex: Int = 10
    ): List<ResultDetailsDto >{
        val username = authentication.name
        return resultService.getAllByUser(username, PageRequest.of(pageIndex, pageSize, resultService.convertSortBy(sortBy, sortOrder)))
    }
    @Transactional
    @GetMapping("/results/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllResults(@RequestParam sortBy: String = "timestamp",
                      @RequestParam sortOrder: String = "DESC",
                      @RequestParam pageSize: Int = 0,
                      @RequestParam pageIndex: Int = 10): List<ResultDetailsDto >{
        return resultService.getAll(PageRequest.of(pageIndex, pageSize, resultService.convertSortBy(sortBy, sortOrder)))
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    fun countResults(authentication: Authentication): Long {
        val username = authentication.name
        return resultService.getCountOfResultsByUser(username)
    }

    @GetMapping("/count/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun countAllResults(): Long {
        return resultService.getCountOfResults()
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