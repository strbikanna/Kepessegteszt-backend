package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.profile_calculation.service.ResultForCalculationDataService
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * This service is responsible for generating recommendations for users based on the result and the information in the games config items.
 */
@Service
class AutoRecommendationStrategy(
    @Autowired private var dataService: ResultForCalculationDataService,
): RecommendationStrategy {

    var log: Logger = LoggerFactory.getLogger(AutoRecommendationStrategy::class.java)

    /**
     * Creates a new recommendation based on the current result and the current config.
     * The config params are incremented to max if the result was successful, decremented otherwise.
     * The recommendation is created by randomly selecting a config item to change.
     */
    @Transactional
    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        recommendedGameId: Long,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
    ): Map<String, Any> =
        withContext(Dispatchers.Default) {
            val game = dataService.getGameWithConfigItems(gameId)
            log.trace("Creating next recommendation based on result for user: ${username}; for game: ${game.name}")
            if(game.configItems.isEmpty()){
                log.error("No config items found for game ${game.name}")
                return@withContext emptyMap()
            }
            val nextRecommendation = previousConfig.toMutableMap()
            val paramsToChange = game.configItems.filter { canChangeParam(nextRecommendation, it, isResultSuccess) }
            if(paramsToChange.isEmpty()){
                return@withContext nextRecommendation
            }
            val nextParamIndex = Math.random().times(paramsToChange.size).toInt()
            val nextParamToChange: ConfigItem = paramsToChange.elementAt(nextParamIndex)
            val currValue = previousConfig[nextParamToChange.paramName] as Int

            if (isResultSuccess) {
                val harderRecommendationParam = recommendHarder(nextParamToChange, currValue)
                nextRecommendation[harderRecommendationParam.first] = harderRecommendationParam.second
            } else {
                val easierRecommendationParam = recommendEasier(nextParamToChange, currValue)
                nextRecommendation[easierRecommendationParam.first] = easierRecommendationParam.second
            }
            log.info("Next recommendation created based on result for user: ${username}; for game: ${game.name}. Config: $nextRecommendation")
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

    private fun recommendEasier(configDescription: ConfigItem, currentValue: Int): Pair<String, Int> {
        if (currentValue == configDescription.easiestValue) return Pair(configDescription.paramName, currentValue)
        return Pair(configDescription.paramName, currentValue - configDescription.increment)
    }

    private fun recommendHarder(configDescription: ConfigItem, currentValue: Int): Pair<String, Int> {
        if (currentValue == configDescription.hardestValue) return Pair(configDescription.paramName, currentValue)
        return Pair(configDescription.paramName, currentValue + configDescription.increment)
    }


}