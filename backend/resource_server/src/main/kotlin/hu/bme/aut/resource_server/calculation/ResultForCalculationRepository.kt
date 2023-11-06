package hu.bme.aut.resource_server.calculation

import hu.bme.aut.resource_server.game.GameEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface ResultForCalculationRepository : PagingAndSortingRepository<ResultForCalculationEntity, Long>, CrudRepository<ResultForCalculationEntity, Long>
{
    fun findAllByGameAndNormalizedResultNotNull(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>
    fun findAllByGameAndNormalizedResultNotNull(game: GameEntity): List<ResultForCalculationEntity>
    fun findAllByGameAndNormalizedResultNull(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>
    fun findAllByGame(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>

    fun countByGame(game: GameEntity): Long
    fun countByGameAndNormalizedResultNull(game: GameEntity): Long
    fun countByGameAndNormalizedResultNotNull(game: GameEntity): Long
}