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
    fun deleteByGameAndNormalizedResultNotNull(game: GameEntity)
    fun deleteByGameAndUserAndNormalizedResultNotNull(game: GameEntity, user: UserEntity)
    fun findAllByGameAndNormalizedResultNull(game: GameEntity, page: Pageable): List<ResultForCalculationEntity>

    fun findAllByGameAndUserAndNormalizedResultNotNull(game: GameEntity, user: UserEntity, page: Pageable): List<ResultForCalculationEntity>
    fun countByGameAndUserAndNormalizedResultNotNull(game: GameEntity, user: UserEntity): Long

    fun countByGame(game: GameEntity): Long
    fun countByGameAndNormalizedResultNull(game: GameEntity): Long
    fun countByGameAndNormalizedResultNotNull(game: GameEntity): Long

    fun findTopByGameAndUserOrderByNormalizedResultDesc(game: GameEntity, user: UserEntity): ResultForCalculationEntity?
    fun findTopByGameAndUserOrderByTimestampDesc(game: GameEntity, user: UserEntity): ResultForCalculationEntity?

    @Procedure("GenerateResultForCalculationData")
    fun generateResultForCalculationProcedure(gameId: Int, abilityCode: String)
}