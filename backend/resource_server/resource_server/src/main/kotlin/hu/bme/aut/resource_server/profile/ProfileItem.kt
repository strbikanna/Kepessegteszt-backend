package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.ability.Ability

sealed class ProfileItem {
    abstract var id: Long?

    abstract val ability: Ability

    abstract val abilityValue: Any
}