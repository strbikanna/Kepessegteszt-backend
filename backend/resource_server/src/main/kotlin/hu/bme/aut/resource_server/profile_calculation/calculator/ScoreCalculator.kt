package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.RuntimeException

object ScoreCalculator {
    private val log: Logger = LoggerFactory.getLogger(ScoreCalculator::class.java)

    private var levelFieldName: String? = "level"
    private var pointsFieldName: String? = "round"
    private var maxPointsFieldName: String? = "maxRound"
    private var extraPointsFieldName: String = "healthPoints"
    private var maxExtraPointsFieldName: String = "maxHealthPoints"
    private var winFieldName: String? = "gameWon"

    val maxNormalizedNonLevelPoints = 20.0
    val levelMultiplicator = 10.0



    fun calculateNormalizedScores(
        results: List<ResultForCalculationEntity>,
        game: GameEntity
    ): List<ResultForCalculationEntity> {
        setFieldNamesFromConfig(game)
        return if (levelFieldName != null && pointsFieldName != null && maxPointsFieldName != null) {
            calculateBasedOnPoints(results)
        } else if (levelFieldName != null && winFieldName != null) {
            calculateBasedOnWin(results)
        } else {
            throw CalculationException("Game cannot be processed, because the config is not set properly!")
        }
    }

    /**
     * calculates the normalized result value based on this equation:
     * NR = L*10 + P/maxP * 10 + E/maxE * 10,
     * where NR - normalized result, L - level, P - points, maxP - max possible points, E - extra points, maxE - max possible extra points
     * For retrieving these values from the result and config JSON, the fieldNames can be set.
     */
    private fun calculateBasedOnPoints(results: List<ResultForCalculationEntity>): List<ResultForCalculationEntity> {
        var L: Double?
        var P: Double?
        var maxP: Double?
        var E: Double?
        var maxE: Double?
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            try {
                L = (result.result[levelFieldName]?.toString())?.toDouble()
                P = (result.result[pointsFieldName]?.toString())?.toDouble()
                maxP = (result.result[maxPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxPointsFieldName]?.toString())?.toDouble()
                E = (result.result[extraPointsFieldName]?.toString())?.toDouble()
                maxE = (result.result[maxExtraPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxExtraPointsFieldName]?.toString())?.toDouble()
                if(E==null && maxE==null) {
                    E = 0.0
                    maxE = 1.0
                }
                if (L != null && P != null && maxP != null && E!= null && maxE != null  && maxE != 0.0 && maxP != 0.0) {
                    result.normalizedResult = L!! * levelMultiplicator + P!! / maxP!! * maxNormalizedNonLevelPoints/2 + E!! / maxE!! * maxNormalizedNonLevelPoints/2
                    normalizedResults.add(result)
                }
            } catch (e: RuntimeException) {
                log.error("Error while calculating normalized score: ${e.message}")
            }

        }

        return normalizedResults
    }

    /**
     * calculates the normalized result value based on this equation:
     * NR = L*10 + 10 if W else L*10,
     * where NR - normalized result, L - level, W - win
     */
    private fun calculateBasedOnWin(results: List<ResultForCalculationEntity>): List<ResultForCalculationEntity> {
        var L: Double?
        var W: Boolean?
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            try {
                L = (result.result[levelFieldName]?.toString())?.toDouble()
                W = (result.result[winFieldName]?.toString())?.toBoolean()
                if (L != null && W != null) {
                    result.normalizedResult = if (W!!) L!! * levelMultiplicator + maxNormalizedNonLevelPoints else L!! * levelMultiplicator
                    normalizedResults.add(result)
                }
            } catch (e: RuntimeException) {
                log.error("Error while calculating normalized score: ${e.message}")
            }
        }
        return normalizedResults
    }

    private fun setFieldNamesFromConfig(game: GameEntity) {
        val config = game.configDescription
        levelFieldName = config["levelFieldName"] as String?
        pointsFieldName = config["pointsFieldName"] as String?
        maxPointsFieldName = config["maxPointsFieldName"] as String?
        extraPointsFieldName = config["extraPointsFieldName"] as String? ?: extraPointsFieldName
        maxExtraPointsFieldName = config["maxExtraPointsFieldName"] as String? ?: maxExtraPointsFieldName
        winFieldName = config["winFieldName"] as String?
    }


    fun getMaxScoreOfLevel(result: ResultForCalculationEntity, game:GameEntity): Double {
        setFieldNamesFromConfig(game)
        return if(maxPointsFieldName != null)
                ((result.result[maxPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxPointsFieldName]?.toString())?.toDouble() ?: 0.0) +
                ((result.result[maxExtraPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxExtraPointsFieldName]?.toString())?.toDouble() ?: 0.0)
        else maxNormalizedNonLevelPoints
    }

    fun getActualScoreOfLevel(result: ResultForCalculationEntity, game: GameEntity): Double {
        setFieldNamesFromConfig(game)
        return if(maxPointsFieldName != null)
                (result.result[pointsFieldName]?.toString()?.toDouble() ?: 0.0) +
                (result.result[extraPointsFieldName]?.toString()?.toDouble() ?: 0.0)
        else result.result[winFieldName]?.toString()?.toBoolean()?.let { if(it) maxNormalizedNonLevelPoints else 0.0 } ?: 0.0
    }

}