package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.ability.AbilityEntity

sealed class ProfileItem {
    abstract var id: Long?

    abstract val ability: AbilityEntity

    abstract val abilityValue: Any
}