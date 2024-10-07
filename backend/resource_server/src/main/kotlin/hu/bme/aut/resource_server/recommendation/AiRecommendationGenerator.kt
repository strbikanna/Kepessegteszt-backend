package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.calculator.AbilityRateCalculatorService
import hu.bme.aut.resource_server.profile_calculation.calculator.ScoreCalculator
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AiRecommendationGenerator(
    @Autowired private var modelManager: ModelManager,
    @Autowired private var dataService: ResultForCalculationDataService,
    @Autowired private var calculatorService: AbilityRateCalculatorService,
) :RecommendationGenerator{

    private var log: Logger = LoggerFactory.getLogger(SimpleRecommendationGenerator::class.java)
    private val motivationRate = 0.7

    override suspend fun createNextRecommendationBasedOnResult(resultId: Long, generatorChain: List<RecommendationGenerator>): Map<String, Any> {
        val result = dataService.getResultById(resultId)
        val user = result.user
        val game = dataService.getGameWithConfigItems(result.recommendedGame.game.id!!)
        log.info("Generating recommendation for user ${user.username} for game ${game.name}")
        if (!modelManager.existsModel(game.id!!)) {
            log.trace("No model found for game ${game.name}.")
            val nextGenerator = generatorChain.firstOrNull()
            return nextGenerator?.createNextRecommendationBasedOnResult(resultId, generatorChain.drop(1)) ?: emptyMap()
        }
        TODO()
    }

    suspend fun createRecommendationModel(gameId: Int) {
        log.trace("Creating neural network model for game with id: $gameId")
        val game = dataService.getGameWithAbilities(gameId)
        val normalizedResults = dataService.getAllNormalizedResultsOfGame(game)
        val abilities = game.affectedAbilities
        val modelInput =
            withContext(Dispatchers.IO) {
                calculatorService.getAbilityValuesAndValuesFromResultsStructured(normalizedResults, abilities.toList())
            }
        modelManager.createNewModel(gameId, modelInput.first, modelInput.second)
        log.info("Recommendation model created for game with id: $gameId")
    }


    /**
     * Generates a recommendation for the given user and game.
     * New unsaved [RecommendedGameEntity] is returned with the recommended level in the config.
     * Recommendation happens based on
     * 1. game model
     * 2. best normalized result of the user
     * 3. latest result of the user
     */
    //TODO make this suspend but take care of lazy loading and transactional issues
    fun generateRecommendationForUser(user: UserEntity, game: GameEntity): RecommendedGameEntity {
        log.info("Generating recommendation for user ${user.username} for game ${game.name}")
        val expectedResult: Double?
        if (!modelManager.existsModel(game.id!!)) {
            log.trace("No model found for game ${game.name}.")
            expectedResult = dataService.getBestResultOfUser(game, user)?.normalizedResult
        } else {
            val profileItems = user.profileFloat
                .filter { game.affectedAbilities.contains(it.ability) }
                .sortedBy { it.ability.code }
                .map { it.abilityValue }
            expectedResult = if (profileItems.size != game.affectedAbilities.size) null
            else try {
                modelManager.getEstimationForResult(game.id!!, profileItems)
            } catch (e: CalculationException) {
                null
            }
        }
        if (expectedResult == null) return generateRecommendationByNotNormalizedResult(user, game)

        val levelPoints = expectedResult - ScoreCalculator.maxNormalizedNonLevelPoints * motivationRate
        var recommendedLevel = ScoreCalculator.getLevelByLevelPoints(levelPoints, game)
        log.trace("Recommended level: $recommendedLevel")
        return RecommendedGameEntity(
            recommendedTo = user,
            game = game,
            config = mapOf("level" to recommendedLevel)
        )
    }

    private fun generateRecommendationByNotNormalizedResult(user: UserEntity, game: GameEntity): RecommendedGameEntity {
        log.trace("Generating recommendation by not normalized result for user ${user.username} for game ${game.name}")
        val latestResult = dataService.getLatestResultOfUser(game, user)
        var recommendation = RecommendedGameEntity(
            recommendedTo = user,
            game = game,
            config = mapOf("level" to 1)
        )
        if (latestResult != null) {
            val maxPossiblePoints = ScoreCalculator.getMaxScoreOfLevel(latestResult, game)
            val points = ScoreCalculator.getActualScoreOfLevel(latestResult, game)
            val successRatio = if (maxPossiblePoints != 0.0) points / maxPossiblePoints else 0.0
            val latestLevel =
                try {
                    latestResult.config["level"]?.toString()?.toInt() ?: latestResult.result["level"]?.toString()
                        ?.toInt() ?: 1
                } catch (e: NumberFormatException) {
                    1
                }
            recommendation = if (successRatio >= motivationRate)
                recommendation.copy(
                    config = mapOf("level" to (latestLevel + 1))
                ) else
                recommendation.copy(
                    config = mapOf("level" to (latestLevel))
                )
        }
        log.trace("Recommended level: {}", recommendation.config["level"])
        return recommendation
    }
}