package hu.bme.aut.auth_server.role

import org.springframework.data.repository.CrudRepository

/**
 * Repository for the role entity with default CRUD methods
 */
interface RoleRepository : CrudRepository<RoleEntity, Int> {
}