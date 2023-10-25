package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.user.UserEntity
import java.time.LocalDateTime

sealed class ProfileSnapshotItem {
    abstract var id: Long?

    abstract val abilityEntity: AbilityEntity

    abstract val timestamp: LocalDateTime?

    abstract val user: UserEntity

    abstract val abilityValue: Any
}