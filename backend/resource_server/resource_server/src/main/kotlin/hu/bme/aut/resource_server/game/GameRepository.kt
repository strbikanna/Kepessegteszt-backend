package hu.bme.aut.resource_server.game

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<GameEntity, Int> {

    fun existsByName(name: String): Boolean

    @Query("SELECT g FROM GameEntity g WHERE g.id = :id")
    fun findGameById(id: Int): Optional<GameEntity>

    @Query("SELECT g FROM GameEntity g WHERE g.name = :name")
    fun findGameByName(name: String): Optional<GameEntity>

    @Modifying
    @Query("DELETE FROM GameEntity g WHERE g.id = :id")
    fun deleteGameById(id: Int): Int

    @Modifying
    @Transactional
    @Query("UPDATE GameEntity g SET g.name = :name, g.description = :description, g.icon = :icon, g.active = :active, g.url = :url, g.jsonDescriptor = :jsonDescriptor  WHERE g.id = :id")
    fun updateGameData(name: String, description: String, icon: String, active: Boolean, url: String, jsonDescriptor: String, id: Int): Int
}