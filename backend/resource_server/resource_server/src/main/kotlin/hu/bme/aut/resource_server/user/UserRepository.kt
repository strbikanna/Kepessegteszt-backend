package hu.bme.aut.resource_server.user

import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository: CrudRepository<UserEntity, Int> {
    fun findByUsername(username: String): Optional<UserEntity>
}