package hu.bme.aut.resource_server.gameplayresult

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface GameplayResultRepository: CrudRepository<GameplayResultEntity, Long> {
    fun findAllByUser(user: UserEntity): List<GameplayResultEntity>
    fun findAllByUserAndTimestampBetween(user: UserEntity, start: LocalDateTime, end: LocalDateTime = LocalDateTime.now()): List<GameplayResultEntity>
}