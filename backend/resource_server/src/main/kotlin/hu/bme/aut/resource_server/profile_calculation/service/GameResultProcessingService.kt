package hu.bme.aut.resource_server.profile_calculation.service

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.calculator.CalculationHelper
import hu.bme.aut.resource_server.profile_calculation.calculator.ScoreCalculator
import hu.bme.aut.resource_server.profile_calculation.data.MeanAndDeviation
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service for processing game results.
 * Responsible for normalizing non-normalized results and keep db consistent.
 */
@Service
class GameResultProcessingService(
    @Autowired private var dataService: ResultForCalculationDataService
) {
    private val defaultPageSize = 1000
    var calculator = ScoreCalculator

    /**
     * Calculates the mean and deviation of the normalized results of the given game.
     * Can be used by both multi- and single ability games.
     */
    @Transactional
    fun processGameResults(gameId: Int): MeanAndDeviation {
            val game = dataService.getGameWithConfigItems(gameId)

            //delete old normalized results of this game
            dataService.deleteAllNormalizedResultsOfGame(game)

            //normalize all non normalized results of this game
            normalizeNewResults(game)

            //calculate median of normalized results of each user
            calculateMedianOfEachUser(game)

            val newNormalizedResultValues = dataService.getAllNormalizedResultsOfGame(game).map { it.normalizedResult!! } //one result for each user
            val mean = CalculationHelper.calculateMean(newNormalizedResultValues)
            val deviation = CalculationHelper.calculateStdDeviation(newNormalizedResultValues, mean)
            return MeanAndDeviation(mean, deviation)
        }



    /**
     * For all not normalized result in the db calculates normalized result.
     * Deletes these not normalized results and saves only the relevant ones of the new normalized values.
     * As a result new relevant normalized values will be in database, having @param timestamp creation timestamp (default to now).
     */
    private fun normalizeNewResults(game: GameEntity, timestamp: LocalDateTime = LocalDateTime.now()){
        val resultCount = dataService.getCountForNewCalculation(game)
        val maxPages: Int = (resultCount/defaultPageSize).toInt()
        var results: List<ResultForCalculationEntity>
        var normalizedResults: List<ResultForCalculationEntity>
        for(i in 0 .. maxPages){
            results = dataService.getAllNonNormalizedResultsOfGame(game, PageRequest.of(i, defaultPageSize))
            normalizedResults = calculator.calculateNormalizedScores(results, game)
            dataService.saveAll( normalizedResults)
        }
    }

    /**
     * Selects all normalized result of game and user and calculates the median of these values.
     * Saves only the median value.
     * Deletes the old normalized values.
     */
    fun calculateMedianOfEachUser(game: GameEntity){
        val userIds = dataService.getAllUserIds()
        userIds.forEach { userId ->
            val medianOfUser = calculateMedianOfUser(game, userId)
            val normalizedResultForUser = ResultForCalculationEntity(
                game = game,
                user = dataService.getUserById(userId),
                normalizedResult = medianOfUser,
                result = mutableMapOf(),
                config = mutableMapOf(),
            )
            dataService.deleteAllNormalizedResultsOfGameAndUser(game, dataService.getUserById(userId))
            dataService.saveAll(listOf(normalizedResultForUser))
        }
    }

    private companion object{
        val sortOrderNormalizedResultDesc = Sort.by(Sort.Order.desc("normalizedResult"))
        val sortOrderNormalizedResultAsc = Sort.by(Sort.Order.asc("normalizedResult"))
        const val PAGE_SIZE_FOR_MEDIAN = 2
    }

    private fun calculateMedianOfUser(game: GameEntity, userId: Int): Double?{
        val user = dataService.getUserById(userId)
        val countOfNormalizedResults = dataService.getCountOfNormalizedResultsByGameAndUser(game, user)
        if(countOfNormalizedResults == 0L)
            return null

        val middleValue: Int = (countOfNormalizedResults/2).toInt()

        val page = PageRequest.of(
            if(middleValue < PAGE_SIZE_FOR_MEDIAN) 0 else (middleValue - PAGE_SIZE_FOR_MEDIAN/2)-1,
            PAGE_SIZE_FOR_MEDIAN,
            sortOrderNormalizedResultAsc
        )

        val normalizedResults = dataService.getNormalizedResultsByGameAndUserPaged(game, user, page)
        var max = normalizedResults.firstOrNull()?.normalizedResult ?: return null
        normalizedResults
            .filter { it.normalizedResult != null }
            .forEach {
            if(it.normalizedResult!! > max)
                max = it.normalizedResult!!
        }
        return max
    }
}