package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime

interface ResultRepository: CrudRepository<ResultEntity, Long> {
    fun findAllByUser(user: UserEntity): List<ResultEntity>
    fun findAllByUserAndTimestampBetween(user: UserEntity, start: LocalDateTime, end: LocalDateTime = LocalDateTime.now()): List<ResultEntity>
}