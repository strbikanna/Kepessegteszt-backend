package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.authentication.AuthService
import kotlinx.coroutines.*
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
        authentication: Authentication
    ): List<RecommendedGameDto> {
        recommenderService.createDefaultRecommendationsForUser(authentication.name)
        return if (pageIndex == null || pageSize == null)
            recommendedGameService.getAllRecommendedToUser(authentication.name)
        else
            recommendedGameService.getAllRecommendedToUser(authentication.name, pageIndex, pageSize)
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'SCIENTIST')")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendationsByGame(
        @RequestParam(required = false) gameId: Int?,
        @RequestParam(required= false) completed: Boolean? = false,
        @RequestParam username: String,
        authentication: Authentication
    ): List<RecommendedGameDto> {
        return recommendedGameService.getRecommendationsToUserAndGame(username, gameId, completed)
    }

    @GetMapping("/config/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGameConfig(@PathVariable id: Long, authentication: Authentication): Deferred<Map<String, Any>> =
        CoroutineScope(Dispatchers.Default).async {
            authService.checkGameConfigAccessAnThrow(id, authentication)
            return@async recommendedGameService.getRecommendedGameConfig(id)
        }

    @GetMapping("/next_choice")
    @ResponseStatus(HttpStatus.OK)
    fun getNextChoiceForUser(authentication: Authentication): List<RecommendedGameDto> {
        return recommendedGameService.getNextChoiceForUser(authentication.name)
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
        val systemRecommendedGames =
            gameplayRecommenderService.getAllRecommendationToUser(authentication.name).toMutableList()
        if (systemRecommendedGames.size < 1) {
            systemRecommendedGames.addAll(gameplayRecommenderService.createNewRecommendations(authentication.name))
        }
        return systemRecommendedGames.map { it.toDto() }
    }

    /**
     * Deletes the current recommendations of the user and creates new ones.
     */
    @PostMapping("/system_recommended")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    fun generateNewRecommendations(authentication: Authentication): List<RecommendedGameDto> {
        val currentActiveRecommendations = gameplayRecommenderService.getAllRecommendationToUser(authentication.name)
        gameplayRecommenderService.deleteRecommendations(currentActiveRecommendations)
        return gameplayRecommenderService.createNewRecommendations(authentication.name).map { it.toDto() }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun deleteRecommendedGame(@PathVariable id: Long, authentication: Authentication) {
        recommendedGameService.deleteRecommendedGame(id)
    }
}