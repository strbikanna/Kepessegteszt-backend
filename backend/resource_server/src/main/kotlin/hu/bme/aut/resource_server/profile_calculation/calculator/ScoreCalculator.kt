package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.RuntimeException
import kotlin.math.roundToInt

object ScoreCalculator {
    private val log: Logger = LoggerFactory.getLogger(ScoreCalculator::class.java)

    private var levelFieldName: String? = "level"
    private var pointsFieldName: String? = "round"
    private var maxPointsFieldName: String? = "maxRound"
    private var extraPointsFieldName: String = "healthPoints"
    private var maxExtraPointsFieldName: String = "maxHealthPoints"
    private var winFieldName: String? = "gameWon"
    private val maxLevelFieldName: String = "maxLevel"

    val maxNormalizedNonLevelPoints = 0.5
    val levelMultiplicator = 2.0



    fun calculateNormalizedScores(
        results: List<ResultForCalculationEntity>,
        game: GameEntity
    ): List<ResultForCalculationEntity> {
        setFieldNamesFromConfig(game)
        return if (levelFieldName != null && pointsFieldName != null && maxPointsFieldName != null) {
            calculateBasedOnPoints(results, game)
        } else if (levelFieldName != null && winFieldName != null) {
            calculateBasedOnWin(results, game)
        } else {
            throw CalculationException("Game cannot be processed, because the config is not set properly!")
        }
    }

    /**
     * calculates the normalized result value based on this equation:
     * NR = (2*L/maxL + P/maxP + E/maxE)/4,
     * where NR - normalized result, L - level, P - points, maxP - max possible points, E - extra points, maxE - max possible extra points
     * For retrieving these values from the result and config JSON, the fieldNames can be set.
     */
    private fun calculateBasedOnPoints(results: List<ResultForCalculationEntity>, game: GameEntity): List<ResultForCalculationEntity> {
        var L: Double?
        var maxL: Double?
        var P: Double?
        var maxP: Double?
        var E: Double?
        var maxE: Double?
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            try {
                L = (result.result[levelFieldName]?.toString())?.toDouble()
                maxL = (game.configDescription[maxLevelFieldName]?.toString())?.toDouble()
                P = (result.result[pointsFieldName]?.toString())?.toDouble()
                maxP = (result.result[maxPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxPointsFieldName]?.toString())?.toDouble()
                E = (result.result[extraPointsFieldName]?.toString())?.toDouble()
                maxE = (result.result[maxExtraPointsFieldName]?.toString())?.toDouble() ?: (result.config[maxExtraPointsFieldName]?.toString())?.toDouble()
                if(E==null && maxE==null) {
                    E = 0.0
                    maxE = 1.0
                }
                if (L != null && maxL != null && P != null && maxP != null && E!= null && maxE != null  && maxE != 0.0 && maxP != 0.0) {
                    result.normalizedResult = (L!!*levelMultiplicator/maxL!! + P!!/maxP!!  + E!!/maxE!!) / (levelMultiplicator + 2)
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
     * NR = (L/maxL + 1)/2 if W else L/maxL,
     * where NR - normalized result, L - level, W - win
     */
    private fun calculateBasedOnWin(results: List<ResultForCalculationEntity>, game: GameEntity): List<ResultForCalculationEntity> {
        var L: Double?
        var maxL: Double?
        var W: Boolean?
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            try {
                L = (result.result[levelFieldName]?.toString())?.toDouble()
                maxL = (game.configDescription[maxLevelFieldName]?.toString())?.toDouble()
                W = (result.result[winFieldName]?.toString())?.toBoolean()
                if (L != null && maxL!=null && W != null) {
                    result.normalizedResult = if (W!!) (L!!/maxL!! + maxNormalizedNonLevelPoints)/2 else L!!/maxL!!
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
        levelFieldName = config["levelFieldName"] as String? ?: levelFieldName
        pointsFieldName = config["pointsFieldName"] as String?
        maxPointsFieldName = config["maxPointsFieldName"] as String?
        extraPointsFieldName = config["extraPointsFieldName"] as String? ?: extraPointsFieldName
        maxExtraPointsFieldName = config["maxExtraPointsFieldName"] as String? ?: maxExtraPointsFieldName
        winFieldName = config["winFieldName"] as String?
    }

    fun getLevelByLevelPoints(levelPoints: Double, game: GameEntity): Int {
        if(levelPoints <= 0) return 1
        setFieldNamesFromConfig(game)
        val maxLevel = (game.configDescription[maxLevelFieldName]?.toString())?.toDouble()
        return if(maxLevel != null)
            (levelPoints * maxLevel * levelMultiplicator).roundToInt()
        else 1
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