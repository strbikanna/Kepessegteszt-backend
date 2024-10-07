package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
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
    @Autowired private var simpleGenerator: SimpleRecommendationGenerator,
    @Autowired private var aiGenerator: AiRecommendationGenerator,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    val log: Logger = LoggerFactory.getLogger(RecommenderService::class.java)

    /**
     * Saves an empty recommendation for the user and the game.
     */
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

    suspend fun createNextRecommendationByResult(gameResult: ResultEntity): Map<String, Any> {
        return simpleGenerator.createNextRecommendationBasedOnResult(gameResult.id!!, listOf(aiGenerator, simpleGenerator))
    }

    /**
     * Creates default recommendations for the user for the active games which have no recommendation.
     */
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

    /**
     * Creates default recommendations for the game for all users even if there is an existing recommendation.
     */
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

    /**
     * Creates a default recommendation for the user for the game.
     */
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
                recommendations.add(aiGenerator.generateRecommendationForUser(user, game))
            }
            recommendedGameRepository.saveAll(recommendations)
        } catch (e: RuntimeException) {
            log.error("Error while generating recommendation for user $username", e)
            return recommendations
        }
        return recommendations
    }

}