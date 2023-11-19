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
    suspend fun createRecommendationModel(gameId: Int){
        val game = dataService.getGameWithAbilities(gameId)
        val normalizedResults = dataService.getAllNormalizedResultsOfGame(game)
        val abilities = game.affectedAbilities
        val modelInput = calculatorService.getAbilityValuesAndValuesFromResultsStructured(normalizedResults, abilities.toList())
        modelManager.createNewModel(gameId, modelInput.first, modelInput.second)
    }
    fun generateRecommendationForUser(user: UserEntity, game: GameEntity): RecommendedGameEntity{
        val expectedResult =
        if(!modelManager.existsModel(game.id!!)) {
             dataService.getBestResultOfUser(game, user)?.normalizedResult ?: 0.0
        }else{
             modelManager.getEstimationForResult(game.id!!,
                user.profileFloat
                    .filter { game.affectedAbilities.contains(it.ability) }
                    .sortedBy { it.ability.code }
                    .map { it.abilityValue }
            )
        }

        val levelPoints = ScoreCalculator.maxNormalizedNonLevelPoints * motivationRate - expectedResult
        var recommendedLevel = if(levelPoints > ScoreCalculator.levelMultiplicator)  levelPoints /ScoreCalculator.levelMultiplicator  else 1.0
        if(levelPoints.mod(ScoreCalculator.levelMultiplicator) > motivationRate * ScoreCalculator.levelMultiplicator) recommendedLevel++

        return RecommendedGameEntity(
            recommendedTo = user,
            game = game,
            config = mapOf("level" to recommendedLevel.toInt())
        )
    }
}