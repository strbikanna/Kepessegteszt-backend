package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface FloatProfileSnapshotRepository : CrudRepository<FloatProfileSnapshotItem, Long> {
    fun findAllByUser(user: UserEntity): List<FloatProfileSnapshotItem>
    fun findAllByUserAndTimestampBetween(user: UserEntity, begin: LocalDateTime, end: LocalDateTime): List<FloatProfileSnapshotItem>
    fun findAllByUserAndAbilityEntityInAndTimestampBetween(user: UserEntity, abilities: List<AbilityEntity>, begin: LocalDateTime, end: LocalDateTime): List<EnumProfileSnapshotItem>

    fun existsByUserAndTimestampBetween(user: UserEntity, begin: LocalDateTime, end: LocalDateTime): Boolean
}