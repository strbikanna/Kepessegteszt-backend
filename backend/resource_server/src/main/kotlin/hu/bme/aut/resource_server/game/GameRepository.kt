package hu.bme.aut.resource_server.game

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<GameEntity, Int> {

    fun findAllByActiveIsTrue(): List<GameEntity>
    fun findGameByName(name: String): Optional<GameEntity>
    fun existsByName(name: String): Boolean

    @Query("SELECT g FROM GameEntity g LEFT JOIN FETCH g.affectedAbilites WHERE g.id = :id")
    fun findByIdWithAbilities(id: Int): Optional<GameEntity>

}