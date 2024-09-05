package hu.bme.aut.resource_server.game

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

/**
 * Repository for the game entity with default CRUD methods
 */
interface GameRepository : CrudRepository<GameEntity, Int>, PagingAndSortingRepository<GameEntity, Int> {

    fun findAllByActiveIsTrue(): List<GameEntity>

    override fun findAll(): List<GameEntity>
    override fun findAll(pageable: Pageable): Page<GameEntity>
    fun findGameByName(name: String): Optional<GameEntity>
    fun existsByName(name: String): Boolean

    @Query("SELECT g FROM GameEntity g LEFT JOIN FETCH g.affectedAbilities WHERE g.id = :id")
    fun findByIdWithAbilities(id: Int): Optional<GameEntity>

    @Query("SELECT g FROM GameEntity g LEFT JOIN FETCH g.configItems WHERE g.id = :id")
    fun findByIdWithConfigItems(id: Int): Optional<GameEntity>

}