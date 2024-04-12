package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameplayResultService(
    @Autowired private var gameplayResultRepository: GameplayResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository

) {
    @Transactional
    fun save(data: GameplayResultDto): GameplayResultEntity {
        val recommendedGame = recommendedGameRepository.findById(data.gameplayId).orElseThrow()
        recommendedGame.completed = true
        recommendedGameRepository.save(recommendedGame)
        val user = recommendedGame.recommendedTo
        val gameplay = GameplayResultEntity(
            result = data.result,
            config = recommendedGame.config.toMutableMap(),
            user = user,
            recommendedGame = recommendedGame
        )
        return gameplayResultRepository.save(gameplay)
    }

    @Transactional
    fun getGameOfResult(resultId: Long): GameEntity {
        val result = gameplayResultRepository.findById(resultId).orElseThrow()
        return result.recommendedGame.game
    }

    fun getAllByUser(username: String): List<GameplayResultEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return gameplayResultRepository.findAllByUser(user)
    }

}