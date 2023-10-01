package hu.bme.aut.resource_server.game

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<GameEntity, Int> {
    fun findGameByName(name: String): Optional<GameEntity>
    fun existsByName(name: String): Boolean
    fun getGamesRecommendedByUserToUser(recommender_id: Int, recommendee_id: Int): List<UserEntity>
}