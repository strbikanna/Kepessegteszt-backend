package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.query.Procedure

interface ResultForCalculationRepository : JpaRepository<ResultForCalculationEntity, Long>
{
    override fun findAll(): List<ResultForCalculationEntity>
    fun findAllByGameAndNormalizedResultNotNull(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>
    fun findAllByGameAndNormalizedResultNotNull(game: GameEntity): List<ResultForCalculationEntity>
    fun findAllByGameAndNormalizedResultNull(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>
    fun findAllByGame(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>

    fun countByGame(game: GameEntity): Long
    fun countByGameAndNormalizedResultNull(game: GameEntity): Long
    fun countByGameAndNormalizedResultNotNull(game: GameEntity): Long

    fun findTopByGameAndUserOrderByNormalizedResultDesc(game: GameEntity, user: UserEntity): ResultForCalculationEntity?
    fun findTopByGameAndUserOrderByTimestampDesc(game: GameEntity, user: UserEntity): ResultForCalculationEntity?

    @Procedure("GenerateResultForCalculationData")
    fun generateResultForCalculationProcedure(gameId: Int, abilityCode: String)
}