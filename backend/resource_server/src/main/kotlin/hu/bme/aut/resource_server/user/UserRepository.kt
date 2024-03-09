package hu.bme.aut.resource_server.user

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository: CrudRepository<UserEntity, Int> {
    fun findByUsername(username: String): Optional<UserEntity>

    fun existsByUsername(username: String): Boolean

    fun findByIdIn(ids: List<Int>): List<UserEntity>

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileFloat LEFT JOIN FETCH u.profileEnum WHERE u.id = :id")
    fun findByIdWithProfile(id: Int): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileFloat LEFT JOIN FETCH u.profileEnum WHERE u.username = :userName")
    fun findByUsernameWithProfile(userName: String): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    fun findByUsernameWithRoles(username: String): Optional<UserEntity>

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.firstName = :firstName, u.lastName = :lastName WHERE u.id = :id")
    fun updateUserData(firstName: String, lastName: String, id: Int): Int
}