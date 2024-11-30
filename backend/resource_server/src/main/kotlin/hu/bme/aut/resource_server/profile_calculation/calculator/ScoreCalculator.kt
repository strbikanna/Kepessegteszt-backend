package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.abs

/**
 * This class is responsible for calculating the normalized score of a result.
 */
object ScoreCalculator {
    private val log: Logger = LoggerFactory.getLogger(ScoreCalculator::class.java)


    /**
     * Calculates the normalized score of the results based on the game config.
     * @throws CalculationException if the game config is not set properly
     */
    fun calculateNormalizedScores(
        results: List<ResultForCalculationEntity>,
        game: GameEntity
    ): List<ResultForCalculationEntity> {
        if (game.configItems.isNotEmpty()) {
            return calculateBasedOnWin(results, game)
        } else {
            throw CalculationException("Game cannot be processed, because the config is not set properly!")
        }
    }


    /**
     * calculates the normalized result value based on this equation:
     * NR = D * S,
     * where NR - normalized result, D - difficulty, S - score and
     * S, D, NR are in the range of [0, 1]
     */
    private fun calculateBasedOnWin(results: List<ResultForCalculationEntity>, game: GameEntity): List<ResultForCalculationEntity> {
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            try {
                val score = getScoreOfResult(result, game)
                val difficulty = getDifficultyOfResult(result, game)
                val normalizedScore = score * difficulty
                result.normalizedResult = normalizedScore
                normalizedResults.add(result)
            } catch (e: RuntimeException) {
                log.error("Error while calculating normalized score: ${e.message}")
            }
        }
        return normalizedResults
    }

    private fun getScoreOfResult(result: ResultForCalculationEntity, game: GameEntity): Double {
        return (result.result["passed"] as Boolean?)?.let { if(it) 1.0 else 0.0 } ?: 0.0
    }

    private fun getDifficultyOfResult(result: ResultForCalculationEntity, game: GameEntity): Double {
        var difficulty = 0.0
        var configItemCount = 0
        game.configItems
            .filter{result.config[it.paramName] is Int}
            .forEach{ configItem ->
            if(result.config.containsKey(configItem.paramName)) {
                val configInResult = abs(configItem.easiestValue - (result.config[configItem.paramName] as Int))
                difficulty += configInResult.toDouble() / abs(configItem.hardestValue - configItem.easiestValue).toDouble()
                configItemCount++
            }
        }
        return if(configItemCount > 0) difficulty / configItemCount else 0.0
    }

}