package hu.bme.aut.auth_server.user

import org.springframework.data.repository.CrudRepository

/**
 * Repository for the user entity with default CRUD methods
 */
interface UserRepository : CrudRepository<UserEntity, Int> {
}