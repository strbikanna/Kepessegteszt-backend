package hu.bme.aut.resource_server.profile_snapshot

import com.fasterxml.jackson.annotation.JsonFormat
import hu.bme.aut.resource_server.ability.AbilityEntity
import java.time.LocalDateTime

data class ProfileSnapshotDto(

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime,

    val profileItems: MutableMap<AbilityEntity, Any>
) {
}