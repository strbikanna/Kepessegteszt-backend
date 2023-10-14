package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository

interface GameplayRepository: CrudRepository<GamePlay, Long> {
    fun findAllByUser(user: UserEntity): List<GamePlay>
}