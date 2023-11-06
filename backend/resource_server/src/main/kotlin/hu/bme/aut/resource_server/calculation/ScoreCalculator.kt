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
        var L: Double?
        var P: Double?
        var maxP: Double?
        var E: Double?
        var maxE: Double?
        val normalizedResults = results.map { result ->
            L = result.result[levelFieldName] as Double?
            P = result.result[pointsFieldName] as Double?
            maxP = result.result[maxPointsFieldName] as Double?
            E = result.result[extraPointsFieldName] as Double?
            maxE = result.result[maxExtraPointsFieldName] as Double?
            if (L == null || P == null || maxP == null || E == null || maxE == null) {
                throw IllegalArgumentException("Result or config JSON does not contain all the necessary fields for calculating normalized result.")
            }
            result.normalizedResult = L!! * 10 + P!! / maxP!! * 10 + E!! / maxE!! * 10
            result
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