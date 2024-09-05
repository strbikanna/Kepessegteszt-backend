package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.result.ResultRepository
import hu.bme.aut.resource_server.user.UserEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

/**
 * Service for handling ResultForCalculationEntity objects and calling repository methods.
 */
@Service
class ResultForCalculationDataService(
    @Autowired private var resultForCalculationRepository: ResultForCalculationRepository,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var resultRepository: ResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository
) {
    private val log = LoggerFactory.getLogger(ResultForCalculationDataService::class.java)
    fun getCountForNewCalculation(gameId: Int): Long {
        val game = getGame(gameId)
        return resultForCalculationRepository.countByGameAndNormalizedResultNull(game)
    }

    /**
     * Returns the count of the non-normalized results for the given game.
     */
    fun getCountForNewCalculation(game: GameEntity): Long =
         resultForCalculationRepository.countByGameAndNormalizedResultNull(game)


    /**
     * Returns the count of the recently processed results for the given game, all
     * of which have a non-null normalized result.
     */
    fun getCountOfRecentCalculation(gameId: Int): Long {
        val game = getGame(gameId)
        return resultForCalculationRepository.countByGameAndNormalizedResultNotNull(game)
    }
    fun getGame(gameId: Int): GameEntity  = gameRepository.findById(gameId).orElseThrow()
    fun getGameWithAbilities(gameId: Int): GameEntity  = gameRepository.findByIdWithAbilities(gameId).orElseThrow()
    fun getGameWithConfigItems(gameId: Int): GameEntity = gameRepository.findByIdWithConfigItems(gameId).orElseThrow()

    fun getAllNormalizedResultsOfGame(game: GameEntity) = resultForCalculationRepository.findAllByGameAndNormalizedResultNotNull(game)
    fun getAllNonNormalizedResultsOfGame(game: GameEntity, page: Pageable) = resultForCalculationRepository.findAllByGameAndNormalizedResultNull(game, page)
    fun delete(result: ResultForCalculationEntity) = resultForCalculationRepository.delete(result)
    fun deleteAll(results: List<ResultForCalculationEntity>) = resultForCalculationRepository.deleteAll(results)
    fun saveAll(results: List<ResultForCalculationEntity>) = resultForCalculationRepository.saveAll(results)

    /**
     * Returns the result with highest normalized result of the given user for the given game.
     */
    fun getBestResultOfUser(game: GameEntity, user: UserEntity) = resultForCalculationRepository.findTopByGameAndUserOrderByNormalizedResultDesc(game, user)

    /**
     * Returns the latest normalized or non-normalized result of the given user for the given game.
     */
    fun getLatestResultOfUser(game: GameEntity, user: UserEntity) = resultForCalculationRepository.findTopByGameAndUserOrderByTimestampDesc(game, user)

    fun getResultById(resultId: Long) = resultRepository.findById(resultId).orElseThrow()

    fun getPreviousRecommendation(currRecommendation: RecommendedGameEntity): RecommendedGameEntity?{
        return recommendedGameRepository.findTopByTimestampBeforeAndRecommendedToAndGameOrderByTimestamp(
            currRecommendation.timestamp!!,
            currRecommendation.recommendedTo,
            currRecommendation.game
        )
    }

}