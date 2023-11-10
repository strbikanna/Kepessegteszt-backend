package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile_calculation.data.MeanAndDeviation
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationRepository
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserProfileUpdaterService(
    @Autowired private var repository: ResultForCalculationRepository,
    @Autowired private var snapshotService: ProfileSnapshotService
) {
    private final val deviationDiffMultiplicator = 0.15
    fun updateUserProfileByResultsOfGame(game: GameEntity, normalizationValue: MeanAndDeviation){
        if(game.affectedAbilites.size == 1){
            updateUserProfilesOneAbility(game, normalizationValue)
        }
    }

    /**
     * Updates the user profiles by the first (and only) affected ability of the game.
     * The new ability value is calculated by the following formula:
     * newAbilityValue = 1 + (normalizedResult - mean) / deviation * 0.15
     */
    private fun updateUserProfilesOneAbility(game: GameEntity, normalizationValue: MeanAndDeviation){
        val affectedAbility = game.affectedAbilites.first()
        val normalizedResults = repository.findAllByGameAndNormalizedResultNotNull(game)
        normalizedResults.forEach { result ->
            val difference = result.normalizedResult!! - normalizationValue.mean
            val abilityValue = 1 + difference/normalizationValue.deviation * deviationDiffMultiplicator
            saveNewAbilityValueOfUser(result.user, affectedAbility, abilityValue)
        }
    }

    /**
     * Updates the user profile with the given ability and value.
     * If previous value exists, snapshot will be taken of user profile and the new value is calculated by the following formula:
     * newAbilityValue = (2 * newValue + oldValue) / 3
     */

    private fun saveNewAbilityValueOfUser(user: UserEntity, ability: AbilityEntity, value: Double){
        val oldProfileItem = user.profileFloat.find { it.ability.code == ability.code }
        val newProfileItem = FloatProfileItem(ability = ability, abilityValue = value)
        if(oldProfileItem != null){
            snapshotService.saveSnapshotOfUserAbilities(user, listOf(ability))
            user.profileFloat.remove(oldProfileItem)
            newProfileItem.abilityValue = (value * 2 + oldProfileItem.abilityValue) /3
        }
        user.profileFloat.add(newProfileItem)
    }


}