package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

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
     * Endpoint to save results of a played game.
     * auth server shall provide auth token for games which contains
     * game id as subject
     * username as claim
     * GAME role as claim
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveResult(@RequestBody gameplayData: GameplayResultDto, authentication: Authentication): GameplayResultEntity {
        authService.checkGameAccessAndThrow(authentication, gameplayData)
        val username = authentication.name
        if(!profileSnapshotService.existsSnapshotToday(username)){
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        //TODO: create next config based on result async and return next recommendation id
        return gameplayResultService.save(gameplayData)
    }

    @Transactional
    @GetMapping("/results")
    @ResponseStatus(HttpStatus.OK)
    fun getResultsByUser(authentication: Authentication): List<GameplayResultEntity>{
        val username = authentication.name
        return gameplayResultService.getAllByUser(username)
    }

}