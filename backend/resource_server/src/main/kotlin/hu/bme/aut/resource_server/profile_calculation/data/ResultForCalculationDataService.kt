package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameRepository
import org.springframework.beans.factory.annotation.Autowired
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
    fun getCountOfRecentCalculation(gameId: Int): Long {
        val game = getGame(gameId)
        return resultForCalculationRepository.countByGameAndNormalizedResultNotNull(game)
    }
    fun getGame(gameId: Int) = gameRepository.findById(gameId).orElseThrow()
}