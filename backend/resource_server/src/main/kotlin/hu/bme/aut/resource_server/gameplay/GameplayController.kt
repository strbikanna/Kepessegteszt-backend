package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for saving gameplay results
 * querying previous game results
 */
@RestController
@RequestMapping("/gameplay")
class GameplayController(
    @Autowired private var gameplayService: GameplayService,
    @Autowired private var profileSnapshotService: ProfileSnapshotService,
    @Autowired private var gameplayRecommenderService: GamePlayRecommenderService,
) {

    /**
     * auth server shall provide auth token for games which contains
     * game id as subject
     * username as claim
     * GAME role as claim
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GAME')")
    fun saveGameplay(@RequestBody gameplayData: GameplayResultDto, authentication: Authentication){
        gameplayService.checkGameAccessAndThrow(authentication, gameplayData)
        val username = gameplayData.username
        if(!profileSnapshotService.existsSnapshotToday(username)){
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        gameplayService.save(gameplayData)
    }

    @GetMapping("/results")
    @ResponseStatus(HttpStatus.OK)
    fun getResultsByUser(authentication: Authentication): List<GameplayEntity>{
        val username = authentication.name
        return gameplayService.getAllByUser(username)
    }

    @GetMapping("/all/system_recommended")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('STUDENT')")
    fun getAllSystemRecommended(authentication: Authentication): List<GameplayDto>{
        return gameplayRecommenderService.getAllRecommendationToUser(authentication.name)
    }

}