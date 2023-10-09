package hu.bme.aut.resource_server.game

import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<Game, Int> {
    fun findGameByName(name: String): Optional<Game>
    fun existsByName(name: String): Boolean

}