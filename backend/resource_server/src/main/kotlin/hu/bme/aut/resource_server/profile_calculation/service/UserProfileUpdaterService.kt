package hu.bme.aut.resource_server.profile_calculation.service

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile_calculation.data.MeanAndDeviation
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.calculator.AbilityRateCalculatorService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.error.ProfileUpdateException
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserService
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service for updating the user cognitive profiles by the normalized results of a game.
 */
@Service
class UserProfileUpdaterService(
    @Autowired private var snapshotService: ProfileSnapshotService,
    @Autowired private var abilityRateCalculatorService: AbilityRateCalculatorService,
    @Autowired private var resultDataService: ResultForCalculationDataService,
    @Autowired private var userService: UserService
) {
    /**
     * Unit of deviation difference's effect on ability value.
     */
    private final val deviationDiffMultiplicator = 0.15

    /**
     * Updates the user profiles by the normalized results of the game with the given id.
     */
    @Transactional
     fun updateUserProfileByResultsOfGame(gameId: Int, normalizationValue: MeanAndDeviation) {
        val game = resultDataService.getGameWithAbilities(gameId)
        if(game.affectedAbilities.isEmpty()){
            throw ProfileUpdateException("Game has no affected abilities.")
        }
        updateUserProfiles(game, normalizationValue)
    }

    /**
     * Updates the user profiles by the first (and only) affected ability of the game.
     * The new ability value is calculated by the following formula:
     * newAbilityValue = 1 + (normalizedResult - mean) / deviation * 0.15
     */
    private fun updateUserProfilesOneAbility(game: GameEntity, normalizationValue: MeanAndDeviation, ability: AbilityEntity){
        val normalizedResults = resultDataService.getAllNormalizedResultsOfGame(game)
        normalizedResults.forEach { result ->
            val difference = result.normalizedResult!! - normalizationValue.mean
            val abilityValue = 1 + (difference/normalizationValue.deviation) * deviationDiffMultiplicator
            val user = userService.getUserEntityWithProfileByUsername(result.user.username)
            saveNewAbilityValueOfUser(user, ability, abilityValue)
        }
    }
    private fun updateUserProfiles(game: GameEntity, normalizationValue: MeanAndDeviation){
        game.affectedAbilities.forEach { ability ->
            updateUserProfilesOneAbility(game, normalizationValue, ability)
        }
    }

    /**
     * Updates the user profile with the given ability and value.
     * If previous value exists, snapshot will be taken of user profile and the new value is calculated by the following formula:
     * newAbilityValue = valueRelevancy * newValue + (1 - valueRelevancy) * oldValue.
     * Value relevancy is a number between 0 and 1 that expresses how much the new value should change the profile.
     */

    private fun saveNewAbilityValueOfUser(user: UserEntity, ability: AbilityEntity, value: Double, valueRelevancy: Double = 0.5){
        val oldProfileItem = user.profileFloat.find { it.ability.code == ability.code }
        val newProfileItem = FloatProfileItem(ability = ability, abilityValue = value)
        if(oldProfileItem != null){
            snapshotService.saveSnapshotOfUserAbilities(user, listOf(ability))
            oldProfileItem.abilityValue = value * valueRelevancy + oldProfileItem.abilityValue * (1.0 - valueRelevancy)
        }else {
            user.profileFloat.add(newProfileItem)
        }
        userService.updateUserProfile(user)
    }

    /**
     * Updates the user profiles by multiple affected abilities of the game.
     * Calls neural network to calculate the ability contributions.
     */
    private fun updateUserProfilesMultiAbility(game: GameEntity, normalizationValue: MeanAndDeviation){
        val abilities = game.affectedAbilities
        val normalizedResults = resultDataService.getAllNormalizedResultsOfGame(game)
        val inputForCalculation = abilityRateCalculatorService.getAbilityValuesAndValuesFromResultsStructured(normalizedResults, abilities.toList())
        val abilityContributions = abilityRateCalculatorService.calculateRates(inputForCalculation.first, inputForCalculation.second)
        normalizedResults.forEach { result ->
            val difference = result.normalizedResult!! - normalizationValue.mean
            val abilityValue = 1 + difference/normalizationValue.deviation * deviationDiffMultiplicator
            abilities.forEachIndexed { index, ability ->
                saveNewAbilityValueOfUser(result.user, ability, abilityValue, abilityContributions[index])
            }
        }

    }


}