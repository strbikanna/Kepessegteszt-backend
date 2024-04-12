package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommended_game.RecommenderService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    @Autowired private var recommenderService: RecommenderService
) {

    /**
     * Endpoint to save results of a played game.
     * Returns the id of the next recommendation which has empty config initially.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun saveResult(@RequestBody gameplayData: GameplayResultDto, authentication: Authentication): Long {
        authService.checkGameAccessAndThrow(authentication, gameplayData)
        val username = authentication.name
        if(!profileSnapshotService.existsSnapshotToday(username)){
            profileSnapshotService.saveSnapshotOfUser(username)
        }
        val savedResult = gameplayResultService.save(gameplayData)
        val game = gameplayResultService.getGameOfResult(savedResult.id!!)
        val nextRecommendation = recommenderService.createEmptyRecommendation(username, game.id!!)
        CoroutineScope(Dispatchers.Default).async {
            delay(5000) //TODO remove later
            val config = recommenderService.createNextRecommendationByResult(savedResult, username)
            nextRecommendation.config = config
            recommenderService.save(nextRecommendation)
        }
        return nextRecommendation.id!!
    }

    @Transactional
    @GetMapping("/results")
    @ResponseStatus(HttpStatus.OK)
    fun getResultsByUser(authentication: Authentication): List<GameplayResultEntity>{
        val username = authentication.name
        return gameplayResultService.getAllByUser(username)
    }

}