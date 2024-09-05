package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommendation.AutoRecommendationService
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommenderService(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var autoRecommender: AutoRecommendationService,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    val log: Logger = LoggerFactory.getLogger(RecommenderService::class.java)

    /**
     * Get all recommendations to user which are not yet completed and the game is active.
     * If the user has no recommendation for a game, a default recommendation is created.
     */
    @Transactional
    fun getAllRecommendationToUser(username: String): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository
            .findAllByRecommendedToAndCompletedAndRecommender(user, false, null)
            .filter { it.game.active }
    }

    @Transactional
    fun createEmptyRecommendation(username: String, gameId: Int): RecommendedGameEntity {
        val user = userRepository.findByUsername(username).orElseThrow()
        val game = gameRepository.findById(gameId).orElseThrow()
        val recommendation = RecommendedGameEntity(
            game = game,
            recommendedTo = user,
            config = emptyMap()
        )
        return recommendedGameRepository.save(recommendation)
    }

    fun save(recommendation: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendation)
    }

    suspend fun createNextRecommendationByResult(gameResult: ResultEntity): Map<String, Any> {
        return autoRecommender.createNextRecommendationBasedOnResult(gameResult.id!!)
    }

    @Transactional
    fun createDefaultRecommendationsForUser(username: String): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val games = gameRepository
            .findAllByActiveIsTrue()
            .filter { game ->
                recommendedGameRepository.findAllByRecommendedToAndGame(user, game)
                    .isEmpty()
            }
        val recommendations = mutableListOf<RecommendedGameEntity>()
        games.forEach { game ->
            recommendations.add(
                RecommendedGameEntity(
                    game = game,
                    recommendedTo = user,
                    config = game.configItems.associateBy({ it.paramName }, { it.initialValue })
                )
            )
        }
        return recommendedGameRepository.saveAll(recommendations).map { it }
    }

    @Transactional
    fun createDefaultRecommendationsForGame(gameId: Int): List<RecommendedGameEntity> {
        val users = userRepository.findAll()
        val game = gameRepository.findById(gameId).orElseThrow()
        val recommendations = mutableListOf<RecommendedGameEntity>()
        users.forEach { user ->
            recommendations.add(
                RecommendedGameEntity(
                    game = game,
                    recommendedTo = user,
                    config = game.configItems.associateBy({ it.paramName }, { it.initialValue })
                )
            )
        }
        return recommendedGameRepository.saveAll(recommendations).map { it }
    }

    @Transactional
    fun createDefaultRecommendationToUserForGame(username: String, gameId: Int): RecommendedGameEntity {
        val user = userRepository.findByUsername(username).orElseThrow()
        val game = gameRepository.findById(gameId).orElseThrow()
        return recommendedGameRepository.save(
            RecommendedGameEntity(
                game = game,
                recommendedTo = user,
                config = game.configItems.associateBy({ it.paramName }, { it.initialValue })
            )
        )
    }

    fun createNewRecommendations(username: String): List<RecommendedGameEntity> {
        val games = gameRepository.findAllByActiveIsTrue()
        val user = userRepository.findByUsername(username).orElseThrow()
        val recommendations = mutableListOf<RecommendedGameEntity>()
        try {
            games.forEach { game ->
                recommendations.add(autoRecommender.generateRecommendationForUser(user, game))
            }
            recommendedGameRepository.saveAll(recommendations)
        } catch (e: RuntimeException) {
            log.error("Error while generating recommendation for user $username", e)
            return recommendations
        }
        return recommendations
    }

    fun deleteRecommendations(recommendations: List<RecommendedGameEntity>) {
        recommendedGameRepository.deleteAll(recommendations)
    }

}