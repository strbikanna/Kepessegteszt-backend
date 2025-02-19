package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface FloatProfileSnapshotRepository : CrudRepository<FloatProfileSnapshotItem, Long>, PagingAndSortingRepository<FloatProfileSnapshotItem, Long> {
    fun findAllByUser(user: UserEntity): List<FloatProfileSnapshotItem>
    fun findAllPagedByUser(user: UserEntity, page: Pageable): List<FloatProfileSnapshotItem>
    fun findAllByUserAndTimestampBetween(user: UserEntity, begin: LocalDateTime, end: LocalDateTime): List<FloatProfileSnapshotItem>
    fun findAllByUserAndAbilityInAndTimestampBetween(user: UserEntity, abilities: List<AbilityEntity>, begin: LocalDateTime, end: LocalDateTime): List<EnumProfileSnapshotItem>

    fun existsByUserAndTimestampBetween(user: UserEntity, begin: LocalDateTime, end: LocalDateTime): Boolean

    fun deleteByUser(user: UserEntity)
}