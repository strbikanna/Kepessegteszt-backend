package hu.bme.aut.resource_server.calculation

import hu.bme.aut.resource_server.ability.AbilityEntity
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

    private fun updateUserProfilesOneAbility(game: GameEntity, normalizationValue: MeanAndDeviation){
        val affectedAbility = game.affectedAbilites.first()
        val normalizedResults = repository.findAllByGameAndNormalizedResultNotNull(game)
        normalizedResults.forEach { result ->
            val difference = result.normalizedResult!! - normalizationValue.mean
            val abilityValue = 1 + difference/normalizationValue.deviation * deviationDiffMultiplicator
            saveNewAbilityValueOfUser(result.user, affectedAbility, abilityValue)
        }
    }

    private fun saveNewAbilityValueOfUser(user: UserEntity, ability: AbilityEntity, value: Double){
        val oldProfileItem = user.profileFloat.find { it.ability.code == ability.code }
        val newProfileItem = FloatProfileItem(ability = ability, abilityValue = value)
        if(oldProfileItem != null){
            snapshotService.saveSnapshotOfUser(user)
            user.profileFloat.remove(oldProfileItem)
            newProfileItem.abilityValue = (value * 2 + oldProfileItem.abilityValue) /3
        }
        user.profileFloat.add(newProfileItem)
    }


}