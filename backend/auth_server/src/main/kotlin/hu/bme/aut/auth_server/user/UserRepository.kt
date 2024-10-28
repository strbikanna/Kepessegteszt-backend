package hu.bme.aut.auth_server.user

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

/**
 * Repository for the user entity with default CRUD methods
 */
interface UserRepository : CrudRepository<UserEntity, Int>, PagingAndSortingRepository<UserEntity, Int> {
    fun findByUsername(username: String): Optional<UserEntity>
    fun existsByUsername(userName: String): Boolean
    fun findByFirstNameLikeOrLastNameLike(
        firstName: String,
        lastName: String,
        pageable: Pageable,
    ): Page<UserEntity>

    fun findByFirstNameLikeAndLastNameLike(
        firstName: String,
        lastName: String,
        pageable: Pageable,
    ): Page<UserEntity>

    @Query(
        value = "select distinct u1.id, u1.username, u1.email, u1.password, u1.first_name, u1.last_name, u1.enabled " +
                "from users u1 " +
                "inner join contacts c on u1.id = c.contact_id " +
                "or u1.id = c.user_id " +
                "inner join users u2 on u2.id = c.contact_id " +
                "or u2.id = c.user_id " +
                "where u2.username = ?1 and u1.username != ?1",
        nativeQuery = true
    )
    fun getContactsByUsername(username: String): List<UserEntity>

    fun findByIdIn(ids: Set<Int>): List<UserEntity>

    override fun findAll(): List<UserEntity>
}