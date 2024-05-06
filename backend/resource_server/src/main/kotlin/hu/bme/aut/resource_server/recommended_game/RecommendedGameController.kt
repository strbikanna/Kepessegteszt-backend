package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.authentication.AuthService
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
    @Autowired private var authService: AuthService
) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGamesToUser(
        @RequestParam(required=false) pageIndex: Int?,
        @RequestParam(required = false) pageSize: Int?,
        authentication: Authentication
    ): List<RecommendedGameDto> {
        if (pageIndex == null || pageSize == null)
            return recommendedGameService.getAllRecommendedToUser(authentication.name)
        else
            return recommendedGameService.getAllRecommendedToUser(authentication.name, pageIndex, pageSize)
    }

    @GetMapping("/config/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGameParams(@PathVariable id: Long, authentication: Authentication): Deferred<Map<String, Any>> = CoroutineScope(Dispatchers.Default).async{
        authService.checkGameConfigAccessAnThrow(id, authentication)
         return@async recommendedGameService.getRecommendedGameConfig(id)
    }

    @PostMapping("/recommend")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun postRecommendedGameToUser(@RequestBody recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameService.addRecommendation(recommendedGame)
    }

    /**
     * Returns the recommended games created by the AutoRecommendationService to the user.
     */
    @GetMapping("/system_recommended")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('STUDENT')")
    fun getAllSystemRecommended(authentication: Authentication): List<RecommendedGameEntity> {
        val systemRecommendedGames = gameplayRecommenderService.getAllRecommendationToUser(authentication.name).toMutableList()
        if(systemRecommendedGames.size < 1){
            systemRecommendedGames.addAll(gameplayRecommenderService.createNewRecommendations(authentication.name))
        }
        return systemRecommendedGames
    }

    /**
     * Deletes the current recommendations of the user and creates new ones.
     */
    @PostMapping("/system_recommended")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('STUDENT')")
    fun generateNewRecommendations(authentication: Authentication): List<RecommendedGameEntity> {
        val currentActiveRecommendations = gameplayRecommenderService.getAllRecommendationToUser(authentication.name)
        gameplayRecommenderService.deleteRecommendations(currentActiveRecommendations)
        return gameplayRecommenderService.createNewRecommendations(authentication.name)
    }
}