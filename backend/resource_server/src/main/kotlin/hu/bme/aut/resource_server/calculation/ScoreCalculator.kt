package hu.bme.aut.resource_server.calculation

import hu.bme.aut.resource_server.game.GameEntity

object ScoreCalculator {

    var levelFieldName = "level"
    var pointsFieldName = "round"
    var maxPointsFieldName = "maxRound"
    var extraPointsFieldName = "healthPoints"
    var maxExtraPointsFieldName = "maxHealthPoints"

    /**
     * calculates the normalized result value based on this equation:
     * NR = L*10 + P/maxP * 10 + E/maxE * 10,
     * where NR - normalized result, L - level, P - points, maxP - max possible points, E - extra points, maxE - max possible extra points
     * For retrieving these values from the result and config JSON, the fieldNames can be set.
     */

    fun calculateNormalizedScores(results: List<ResultForCalculationEntity>): List<ResultForCalculationEntity> {
        var L: Int?
        var P: Int?
        var maxP: Int?
        var E: Int?
        var maxE: Int?
        val normalizedResults = mutableListOf<ResultForCalculationEntity>()
        results.forEach { result ->
            L = result.result[levelFieldName] as Int?
            P = result.result[pointsFieldName] as Int?
            maxP = result.result[maxPointsFieldName] as Int?
            E = result.result[extraPointsFieldName] as Int?
            maxE = result.result[maxExtraPointsFieldName] as Int?
            if (L != null && P != null && maxP != null && E != null && maxE != null) {
                result.normalizedResult = L!!.toDouble() * 10 + P!!.toDouble() / maxP!!.toDouble() * 10 + E!!.toDouble() / maxE!!.toDouble() * 10
                normalizedResults.add(result)
            }
        }
        return normalizedResults
    }

    fun setFieldNamesFromConfig(game: GameEntity) {
        val config = game.configDescription
        levelFieldName = config["levelFieldName"] as String? ?: levelFieldName
        pointsFieldName = config["pointsFieldName"] as String? ?: pointsFieldName
        maxPointsFieldName = config["maxPointsFieldName"] as String? ?: maxPointsFieldName
        extraPointsFieldName = config["extraPointsFieldName"] as String? ?: extraPointsFieldName
        maxExtraPointsFieldName = config["maxExtraPointsFieldName"] as String? ?: maxExtraPointsFieldName
    }
}