package hu.bme.aut.resource_server.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository: CrudRepository<UserEntity, Int> {
    fun findByUsername(username: String): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.profile WHERE u.id = :id")
    fun findByIdWithProfile(id: Int): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.id = :id")
    fun findByIdWithRoles(id: Int): Optional<UserEntity>
}