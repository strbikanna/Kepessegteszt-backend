package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.profile_calculation.calculator.AbilityRateCalculatorService
import hu.bme.aut.resource_server.profile_calculation.calculator.ScoreCalculator
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This service is responsible for generating recommendations for users.
 * It uses the [ResultForCalculationDataService] to get the results of the user and the [AbilityRateCalculatorService]
 * to calculate the ability values from the results.
 * It uses the [ModelManager] to get the recommendation model for the game.
 */
@Service
class AutoRecommendationService(
    @Autowired private var dataService: ResultForCalculationDataService,
    @Autowired private var calculatorService: AbilityRateCalculatorService,
    @Autowired private var modelManager: ModelManager,
) {
    val motivationRate = 0.7

    var log: Logger = LoggerFactory.getLogger(AutoRecommendationService::class.java)

    suspend fun createRecommendationModel(gameId: Int) {
        log.trace("Creating recommendation model for game with id: $gameId")
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
     * Creates a new recommendation based on the current result and the previous and current recommendation.
     * The config parameters are changed in their defined order.
     * The config params are incremented to max if the result was successful, decremented otherwise.
     * After reaching max or min, the next parameter is changed and the value is set to the initial value.
     */
    @Transactional
    suspend fun createNextRecommendationBasedOnResult(resultId: Long): Map<String, Any> =
        withContext(Dispatchers.Default) {
            val result = dataService.getResultById(resultId)
            val user = result.user
            val game = dataService.getGameWithConfigItems(result.recommendedGame.game.id!!)
            log.trace("Creating next recommendation based on result for user: ${user.username}; for game: ${game.name}")
            if(game.configItems.isEmpty()){
                log.info("No config items found for game ${game.name}")
                return@withContext emptyMap()
            }
            val success = isResultSuccess(result)
            val nextRecommendation = result.recommendedGame.config.toMutableMap()
            val paramsToChange = game.configItems.filter { canChangeParam(nextRecommendation, it, success) }
            if(paramsToChange.isEmpty()){
                return@withContext nextRecommendation
            }
            val nextParamIndex = Math.random().times(paramsToChange.size).toInt()
            val nextParamToChange: ConfigItem = paramsToChange.elementAt(nextParamIndex)
            val currValue = result.recommendedGame.config[nextParamToChange.paramName] as Int

            if (success) {
                val harderRecommendationParam = recommendHarder(nextParamToChange, currValue)
                nextRecommendation[harderRecommendationParam.first] = harderRecommendationParam.second
            } else {
                val easierRecommendationParam = recommendEasier(nextParamToChange, currValue)
                nextRecommendation[easierRecommendationParam.first] = easierRecommendationParam.second
            }
            log.info("Next recommendation created based on result for user: ${user.username}; for game: ${game.name}. Config: $nextRecommendation")
            return@withContext nextRecommendation
        }

    private fun canChangeParam(
        currentRecommendation: MutableMap<String, Any>,
        param: ConfigItem,
        success: Boolean
    ) : Boolean {
        return if (success) {
                ((currentRecommendation[param.paramName] as Int) + param.increment <= param.hardestValue && param.increment > 0 ) ||
                        ((currentRecommendation[param.paramName] as Int) + param.increment >= param.hardestValue && param.increment < 0)
        }else{
                (currentRecommendation[param.paramName] as Int) - param.increment >= param.easiestValue && param.increment > 0 ||
                        (currentRecommendation[param.paramName] as Int) - param.increment <= param.easiestValue && param.increment < 0
        }
    }

    private fun isResultSuccess(result: ResultEntity): Boolean{
        return result.result["passed"] as Boolean
    }

    private fun recommendEasier(configDescription: ConfigItem, currentValue: Int): Pair<String, Int> {
        if (currentValue == configDescription.easiestValue) return Pair(configDescription.paramName, currentValue)
        return Pair(configDescription.paramName, currentValue - configDescription.increment)
    }

    private fun recommendHarder(configDescription: ConfigItem, currentValue: Int): Pair<String, Int> {
        if (currentValue == configDescription.hardestValue) return Pair(configDescription.paramName, currentValue)
        return Pair(configDescription.paramName, currentValue + configDescription.increment)
    }

    /**
     * Generates a recommendation for the given user and game.
     * New unsaved [RecommendedGameEntity] is returned with the recommended level in the config.
     * Recommendation happens based on
     * 1. game model
     * 2. best normalized result of the user
     * 3. latest result of the user
     */
    //TODO use neural network model
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

        return RecommendedGameEntity(
            recommendedTo = user,
            game = game,
            config = game.configItems.associateBy({ it.paramName }, { it.initialValue })
        )
    }

}