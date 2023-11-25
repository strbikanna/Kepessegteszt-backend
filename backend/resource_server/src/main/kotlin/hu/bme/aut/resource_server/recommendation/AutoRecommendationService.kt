package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.calculator.AbilityRateCalculatorService
import hu.bme.aut.resource_server.profile_calculation.calculator.ScoreCalculator
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AutoRecommendationService(
    @Autowired private var dataService: ResultForCalculationDataService,
    @Autowired private var calculatorService: AbilityRateCalculatorService,
    @Autowired private var modelManager: ModelManager
) {
    val motivationRate = 0.7
    suspend fun createRecommendationModel(gameId: Int) {
        val game = dataService.getGameWithAbilities(gameId)
        val normalizedResults = dataService.getAllNormalizedResultsOfGame(game)
        val abilities = game.affectedAbilities
        val modelInput =
            calculatorService.getAbilityValuesAndValuesFromResultsStructured(normalizedResults, abilities.toList())
        modelManager.createNewModel(gameId, modelInput.first, modelInput.second)
    }

    fun generateRecommendationForUser(user: UserEntity, game: GameEntity): RecommendedGameEntity {
        val expectedResult: Double?
        if (!modelManager.existsModel(game.id!!)) {
            expectedResult = dataService.getBestResultOfUser(game, user)?.normalizedResult
        } else {
            val profileItems = user.profileFloat
                .filter { game.affectedAbilities.contains(it.ability) }
                .sortedBy { it.ability.code }
                .map { it.abilityValue }
            expectedResult = if (profileItems.size != game.affectedAbilities.size) null
            else modelManager.getEstimationForResult(game.id!!, profileItems)
        }
        if (expectedResult == null) return generateRecommendationByNotNormalizedResult(user, game)

        val levelPoints = expectedResult - ScoreCalculator.maxNormalizedNonLevelPoints * motivationRate
        var recommendedLevel =
            if (levelPoints > ScoreCalculator.levelMultiplicator) levelPoints / ScoreCalculator.levelMultiplicator else 1.0
        if (levelPoints.mod(ScoreCalculator.levelMultiplicator) > motivationRate * ScoreCalculator.levelMultiplicator) recommendedLevel++

        return RecommendedGameEntity(
            recommendedTo = user,
            game = game,
            config = mapOf("level" to recommendedLevel.toInt())
        )
    }

    private fun generateRecommendationByNotNormalizedResult(user: UserEntity, game: GameEntity): RecommendedGameEntity {
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
                try{
                    latestResult.config["level"]?.toString()?.toInt() ?: latestResult.result["level"]?.toString()?.toInt() ?: 1
                }catch (e: NumberFormatException){1}
            recommendation = if (successRatio >= motivationRate)
                recommendation.copy(
                    config = mapOf("level" to (latestLevel + 1))
                ) else
                recommendation.copy(
                    config = mapOf("level" to (latestLevel))
                )
        }
        return recommendation
    }
}