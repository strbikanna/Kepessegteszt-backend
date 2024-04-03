package hu.bme.aut.resource_server.recommended_game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/recommended_game")
class RecommendedGameController(
    @Autowired private var recommendedGameService: RecommendedGameService,
    @Autowired private var gameplayRecommenderService: RecommenderService
) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGamesToUser(
        @RequestParam(required=false) pageIndex: Int?,
        @RequestParam(required = false) pageSize: Int?,
        authentication: Authentication
    ): List<RecommendedGameEntity> {
        if (pageIndex == null || pageSize == null)
            return recommendedGameService.getAllRecommendedToUser(authentication.name)
        else
            return recommendedGameService.getAllRecommendedToUser(authentication.name, pageIndex, pageSize)
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
        if(systemRecommendedGames.size < 3){
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