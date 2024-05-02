package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResultService(
    @Autowired private var resultRepository: ResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository

) {
    @Transactional
    fun save(data: ResultDto): ResultEntity {
        val recommendedGame = recommendedGameRepository.findById(data.gameplayId).orElseThrow()
        recommendedGame.completed = true
        recommendedGameRepository.save(recommendedGame)
        val user = recommendedGame.recommendedTo
        val gameplay = ResultEntity(
            result = data.result,
            config = recommendedGame.config.toMutableMap(),
            user = user,
            recommendedGame = recommendedGame
        )
        return resultRepository.save(gameplay)
    }

    @Transactional
    fun getGameOfResult(resultId: Long): GameEntity {
        val result = resultRepository.findById(resultId).orElseThrow()
        return result.recommendedGame.game
    }

    fun getAllByUser(username: String): List<ResultEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return resultRepository.findAllByUser(user)
    }

    @Transactional
    fun getNextRecommendationForGameIfExists(recommendationId: Long, username: String): RecommendedGameEntity? {
        val user = userRepository.findByUsername(username).orElseThrow()
        val game = recommendedGameRepository.findById(recommendationId).orElseThrow().game
        return recommendedGameRepository.findByRecommendedToAndGameAndCompletedAndRecommender(user, game, false, null)
    }

}