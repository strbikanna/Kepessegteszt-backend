package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ResultForCalculationDataService(
    @Autowired private var resultForCalculationRepository: ResultForCalculationRepository,
    @Autowired private var gameRepository: GameRepository
) {
    fun getCountForNewCalculation(gameId: Int): Long {
        val game = getGame(gameId)
        return resultForCalculationRepository.countByGameAndNormalizedResultNull(game)
    }
    fun getCountForNewCalculation(game: GameEntity): Long =
         resultForCalculationRepository.countByGameAndNormalizedResultNull(game)


    fun getCountOfRecentCalculation(gameId: Int): Long {
        val game = getGame(gameId)
        return resultForCalculationRepository.countByGameAndNormalizedResultNotNull(game)
    }
    fun getGame(gameId: Int): GameEntity  = gameRepository.findById(gameId).orElseThrow()
    fun getGameWithAbilities(gameId: Int): GameEntity  = gameRepository.findByIdWithAbilities(gameId).orElseThrow()

    fun getAllNormalizedResultsOfGame(game: GameEntity) = resultForCalculationRepository.findAllByGameAndNormalizedResultNotNull(game)
    fun getAllNonNormalizedResultsOfGame(game: GameEntity, page: Pageable) = resultForCalculationRepository.findAllByGameAndNormalizedResultNull(game, page)
    fun delete(result: ResultForCalculationEntity) = resultForCalculationRepository.delete(result)
    fun deleteAll(results: List<ResultForCalculationEntity>) = resultForCalculationRepository.deleteAll(results)
    fun saveAll(results: List<ResultForCalculationEntity>) = resultForCalculationRepository.saveAll(results)

    fun getBestResultOfUser(game: GameEntity, user: UserEntity) = resultForCalculationRepository.findTopByGameAndUserOrderByNormalizedResultDesc(game, user)
}