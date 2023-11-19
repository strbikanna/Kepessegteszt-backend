package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommended_game.RecommenderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for saving gameplay results and
 * querying previous game results
 */
@RestController
@RequestMapping("/gameplay")
class GameplayResultController(
    @Autowired private var gameplayResultService: GameplayResultService,
    @Autowired private var profileSnapshotService: ProfileSnapshotService,
    @Autowired private var authService: AuthService,
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
    fun saveResult(@RequestBody gameplayData: GameplayResultDto, authentication: Authentication){
        authService.checkGameAccessAndThrow(authentication, gameplayData)
        val username = gameplayData.username
        if(!profileSnapshotService.existsSnapshotToday(username)){
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        gameplayResultService.save(gameplayData)
    }

    @Transactional
    @GetMapping("/results")
    @ResponseStatus(HttpStatus.OK)
    fun getResultsByUser(authentication: Authentication): List<GameplayResultEntity>{
        val username = authentication.name
        return gameplayResultService.getAllByUser(username)
    }



}