package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.recommendation.RecommenderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/recommended_game")
class RecommendedGameController(
    @Autowired private var recommendedGameService: RecommendedGameService,
    @Autowired private var gameplayRecommenderService: RecommenderService,
    @Autowired private var recommenderService: RecommenderService,
    @Autowired private var authService: AuthService
) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGamesToUser(
        @RequestParam(required = false) pageIndex: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) acceptedGameIds: List<Int>?,
        authentication: Authentication
    ): List<RecommendedGameDto> {
        recommenderService.createDefaultRecommendationsForUser(authentication.name)
        return if (pageIndex == null || pageSize == null)
            recommendedGameService.getAllRecommendedToUser(authentication.name, acceptedGameIds)
        else
            recommendedGameService.getAllRecommendedToUser(authentication.name, acceptedGameIds, pageIndex, pageSize)
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'SCIENTIST')")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendationsByGame(
        @RequestParam(required = false) gameId: Int?,
        @RequestParam(required= false) completed: Boolean? = false,
        @RequestParam username: String,
    ): List<RecommendedGameDto> {
        return recommendedGameService.getRecommendationsToUserAndGame(username, gameId, completed)
    }

    @GetMapping("/config/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGameConfig(@PathVariable id: Long, authentication: Authentication): Deferred<Map<String, Any>> =
        CoroutineScope(Dispatchers.Default).async {
            authService.checkGameConfigAccessAndThrow(id, authentication)
            val foundConfig = recommendedGameService.getRecommendedGameConfig(id)
            return@async foundConfig ?: emptyMap()
        }

    @GetMapping("/next_choice")
    @ResponseStatus(HttpStatus.OK)
    fun getNextChoiceForUser(
        @RequestParam(required = false) acceptedGameIds: List<Int>?,
        authentication: Authentication
    ): List<RecommendedGameDto> {
        return recommendedGameService.getNextChoiceForUser(authentication.name, acceptedGameIds)
    }

    @PostMapping("/recommend")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun postRecommendedGameToUser(
        @RequestBody recommendedGame: RecommendationDto,
        authentication: Authentication
    ): Deferred<RecommendedGameDto> =
        authService.doIfIsContact(
            authentication,
            recommendedGame.recommendedTo
        ) {
            recommendedGameService.addRecommendation(recommendedGame, authentication.name).toDto()
        }


    /**
     * Returns the recommended games created by the AutoRecommendationService to the user.
     */
    @GetMapping("/system_recommended")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('STUDENT')")
    fun getAllSystemRecommended(authentication: Authentication): List<RecommendedGameDto> {
        val systemRecommendedGames = gameplayRecommenderService.getAllRecommendationToUser(authentication.name).toMutableList()
        return systemRecommendedGames.map { it.toDto() }
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun deleteRecommendedGame(@PathVariable id: Long, authentication: Authentication) {
        recommendedGameService.deleteRecommendedGame(id)
    }
}