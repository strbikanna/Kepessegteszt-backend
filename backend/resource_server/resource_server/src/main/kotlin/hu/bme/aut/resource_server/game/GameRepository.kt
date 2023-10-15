package hu.bme.aut.resource_server.game

import org.springframework.data.repository.CrudRepository

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<GameEntity, Int> {

}