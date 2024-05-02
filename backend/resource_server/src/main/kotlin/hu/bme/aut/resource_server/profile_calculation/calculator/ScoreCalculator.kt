package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.roundToInt

/**
 * This class is responsible for calculating the normalized score of a result.
 */
object ScoreCalculator {
    private val log: Logger = LoggerFactory.getLogger(ScoreCalculator::class.java)

    private var levelFieldName: String? = "level"
    private var pointsFieldName: String? = "round"
    private var maxPointsFieldName: String? = "maxRound"
    private var extraPointsFieldName: String = "healthPoints"
    private var maxExtraPointsFieldName: String = "maxHealthPoints"
    private var winFieldName: String? = "gameWon"
    private const val maxLevelFieldName: String = "maxLevel"

    const val maxNormalizedNonLevelPoints = 0.5
    const val levelMultiplicator = 2.0


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
        return (result.result["passed"] as Boolean).let { if(it) 1.0 else 0.0 }
    }

    private fun getDifficultyOfResult(result: ResultForCalculationEntity, game: GameEntity): Double {
        var difficulty = 0.0
        var configItemCount = 0
        game.configItems.forEach() { configItem ->
            if(result.config.containsKey(configItem.paramName)) {
                val configInResult =
                    if(configItem.hardestValue > configItem.easiestValue) {
                        (result.config[configItem.paramName] as Double)
                    }else{
                        configItem.easiestValue - (result.config[configItem.paramName] as Double) //distance from easiest value
                    }
                difficulty += configInResult / configItem.hardestValue
                configItemCount++
            }
        }
        return if(configItemCount > 0) difficulty / configItemCount else 0.0
    }

    private fun setFieldNamesFromConfig(game: GameEntity) {
        val config = game.configDescription
        levelFieldName = config["levelFieldName"] as String? ?: "level"
        pointsFieldName = config["pointsFieldName"] as String?
        maxPointsFieldName = config["maxPointsFieldName"] as String?
        extraPointsFieldName = config["extraPointsFieldName"] as String? ?: extraPointsFieldName
        maxExtraPointsFieldName = config["maxExtraPointsFieldName"] as String? ?: maxExtraPointsFieldName
        winFieldName = config["winFieldName"] as String?
    }

    /**
     * Returns the level based on the levelPoints of the normalized result.
     * (Inverse of the normalized level score calculation)
     */
    fun getLevelByLevelPoints(levelPoints: Double, game: GameEntity): Int {
        if(levelPoints <= 0) return 1
        setFieldNamesFromConfig(game)
        val maxLevel = (game.configDescription[maxLevelFieldName]?.toString())?.toDouble()
        return if(maxLevel != null)
            (levelPoints * maxLevel * levelMultiplicator).roundToInt()
        else 1
    }

    /**
     * Returns the max possible non-normalized points in a level.
     */
    fun getMaxScoreOfLevel(result: ResultForCalculationEntity, game:GameEntity): Double {
        setFieldNamesFromConfig(game)
        return if(maxPointsFieldName != null)
                ((result.result[maxPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxPointsFieldName]?.toString())?.toDouble() ?: 0.0) +
                ((result.result[maxExtraPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxExtraPointsFieldName]?.toString())?.toDouble() ?: 0.0)
        else maxNormalizedNonLevelPoints
    }

    /**
     * Returns the actual non-normalized points in a level.
     */
    fun getActualScoreOfLevel(result: ResultForCalculationEntity, game: GameEntity): Double {
        setFieldNamesFromConfig(game)
        return if(maxPointsFieldName != null)
                (result.result[pointsFieldName]?.toString()?.toDouble() ?: 0.0) +
                (result.result[extraPointsFieldName]?.toString()?.toDouble() ?: 0.0)
        else result.result[winFieldName]?.toString()?.toBoolean()?.let { if(it) maxNormalizedNonLevelPoints else 0.0 } ?: 0.0
    }

}