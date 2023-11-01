package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameplayResultService(
    @Autowired private var gameplayResultRepository: GameplayResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var userRepository: UserRepository

) {
    @Transactional
    fun save(data: GameplayResultDto){
        val recommendedGame = recommendedGameRepository.findById(data.gameplayId).orElseThrow()
        recommendedGame.completed = true
        recommendedGameRepository.save(recommendedGame)
        val user = userRepository.findByUsername(data.username).orElseThrow()
        val gameplay = GameplayResultEntity(
            result = data.gameResult,
            config = data.config.toMutableMap(),
            user = user,
            recommendedGame = recommendedGame
        )
        gameplayResultRepository.save(gameplay)
    }

    fun getAllByUser(username: String): List<GameplayResultEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return gameplayResultRepository.findAllByUser(user)
    }

    /**
     * Checks if the gameplay is authorized to save results for the user.
     * @throws IllegalAccessException if the game_id in the token is either not present
     * or not matching any game in the database.
     */
    @Transactional
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: GameplayResultDto){
        val username = authentication.name
        val gamePlay = recommendedGameRepository.findById(gameplay.gameplayId)
        val jwt = authentication.principal as Jwt
        val tokenGameId = jwt.claims["game_id"] as Int?
            ?: throw IllegalAccessException("This gameplay is not authorized to save result for this user.")
        val game = gameRepository.findById(tokenGameId)
        if(gamePlay.isEmpty || username == null ||game.isEmpty || tokenGameId != gamePlay.get().game.id){
            throw IllegalAccessException("This gameplay is not authorized to save result for this user.")
        }
    }

}