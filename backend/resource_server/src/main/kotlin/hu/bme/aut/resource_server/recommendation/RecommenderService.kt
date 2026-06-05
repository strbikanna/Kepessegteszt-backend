package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommendation.special_settings.DISTRACTION_CONFIG_KEY
import hu.bme.aut.resource_server.recommendation.special_settings.SpecialSettingsDto
import hu.bme.aut.resource_server.recommendation.special_settings.XP_CONFIG_KEY
import hu.bme.aut.resource_server.recommendation.strategy.*
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.BusinessCritical
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommenderService(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var autoRecommendationStrategy: AutoRecommendationStrategy,
    @Autowired private var suggestApiStrategy: SuggestApiStrategy,
    @Autowired private var latestRecommendationStrategy: LatestRecommendationStrategy,
    @Autowired private var defaultRecommendationStrategy: DefaultRecommendationStrategy,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    val log: Logger = LoggerFactory.getLogger(RecommenderService::class.java)

    private lateinit var recommendationStrategies: List<RecommendationStrategy>

    @PostConstruct
    fun initRecommendationStrategies() {
        recommendationStrategies = listOf(
            suggestApiStrategy,
            autoRecommendationStrategy,
            latestRecommendationStrategy,
            defaultRecommendationStrategy
        )
    }

    /**
     * Get all recommendations to user which are not yet completed and the game is active.
     * If the user has no recommendation for a game, a default recommendation is created.
     */
    @Transactional
    @BusinessCritical
    fun getAllRecommendationToUser(username: String): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository
            .findAllByRecommendedToAndCompleted(user, false)
            .filter { it.game.active }
    }

    /**
     * Saves an empty recommendation for the user and the game.
     */
    @Transactional
    @BusinessCritical
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

    @BusinessCritical
    fun save(recommendation: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendation)
    }

    @Transactional
    @BusinessCritical
    suspend fun createNextRecommendationByResult(gameResult: ResultEntity): Map<String, Any> {
        recommendationStrategies.forEach {
            try {
                val config = it.generateRecommendationByResult(
                    gameResult.recommendedGame.recommendedTo.username,
                    gameResult.recommendedGame.game.id!!,
                    gameResult.recommendedGame.id!!,
                    gameResult.config,
                    gameResult.passed
                )
                if (config.isNotEmpty()) {
                    return config
                }
            } catch (e: Exception) {
                log.error("Error while generating recommendation by result: $e")
            }
        }
        //none of the recommendations were successful, technically never should happen
        return emptyMap()
    }

    /**
     * Creates default recommendations for the user for the active games which have no recommendation.
     */
    @Transactional
    @BusinessCritical
    fun createDefaultRecommendationsForUser(username: String): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val games = gameRepository
            .findAllByActiveIsTrue()
            .filter { game ->
                //there are no recommendation for the game and the user which is not completed
                recommendedGameRepository.findAllByRecommendedToAndGameAndCompleted(user, game, false)
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
    @BusinessCritical
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

    @BusinessCritical
    fun applySpecialSettings(config: Map<String, Any>, gameId: Int, username: String): Map<String, Any> {
        val user = userRepository.findByUsernameWithSpecialSettings(username).orElseThrow()
        val validSettings = user.specialGameSettings.filter { it.isValid() }.toMutableSet()
        val game = gameRepository.findByIdWithConfigItems(gameId).orElseThrow()
        val xpGain = XPCalculator.calculateXP(game.configItems, config)
        val updatedConfig = config.toMutableMap()
        updatedConfig[DISTRACTION_CONFIG_KEY] = SpecialSettingsDto(validSettings)
        updatedConfig[XP_CONFIG_KEY] = xpGain
        return updatedConfig
    }


}
