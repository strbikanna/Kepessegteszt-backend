package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface GameplayRepository: CrudRepository<GameplayEntity, Long> {
    fun findAllByUser(user: UserEntity): List<GameplayEntity>
    fun findAllByUserAndTimestampBetween(user: UserEntity, start: LocalDateTime, end: LocalDateTime = LocalDateTime.now()): List<GameplayEntity>
}