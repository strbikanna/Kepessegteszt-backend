package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResultForCalculationDataService(
    @Autowired private var resultForCalculationRepository: ResultForCalculationRepository,
    @Autowired private var gameRepository: GameRepository
) {
    private val log = LoggerFactory.getLogger(ResultForCalculationDataService::class.java)
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
    fun getLatestResultOfUser(game: GameEntity, user: UserEntity) = resultForCalculationRepository.findTopByGameAndUserOrderByTimestampDesc(game, user)

    @Scheduled(cron = "0 0 * * * *" )
    @Transactional
    fun insertCalcDataEachHour() {
        var game = gameRepository.findGameByName("Meteorháború")
        if(game.isEmpty) game = gameRepository.findGameByName("cosmic-sequence")
        if(game.isEmpty) game = gameRepository.findGameByName("CosmicSequence")
        if(game.isEmpty){
            log.error("Game for calculation data insert not found.")
            return
        }
        val currentNonNormalizedResultsCount = resultForCalculationRepository.countByGameAndNormalizedResultNull(game.get())
        if(currentNonNormalizedResultsCount < 800){
            log.info("Generating calculation data for game ${game.get().name}")
            resultForCalculationRepository
                .generateResultForCalculationProcedure(
                    game.get().id!!,
                    game.get().affectedAbilities.first().code
                )
            val countAfterInsert = resultForCalculationRepository.countByGameAndNormalizedResultNull(game.get())
            log.info("Calculation data generated for game ${game.get().name}. Inserted ${countAfterInsert - currentNonNormalizedResultsCount} new rows.")
        }
    }
}