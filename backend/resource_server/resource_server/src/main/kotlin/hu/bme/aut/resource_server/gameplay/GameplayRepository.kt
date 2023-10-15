package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface GameplayRepository: CrudRepository<GamePlay, Long> {
    fun findAllByUser(user: UserEntity): List<GamePlay>
    fun findAllByUserAndTimestampBetween(user: UserEntity, start: LocalDateTime, end: LocalDateTime = LocalDateTime.now()): List<GamePlay>
}