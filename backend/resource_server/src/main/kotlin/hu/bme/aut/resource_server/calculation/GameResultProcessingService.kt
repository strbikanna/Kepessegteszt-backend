package hu.bme.aut.resource_server.calculation

import hu.bme.aut.resource_server.game.GameEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GameResultProcessingService(
    @Autowired private var repository: ResultForCalculationRepository
) {
    private val defaultPageSize = 1000

    fun processGameResults(game: GameEntity): MeanAndDeviation{
        val normalizationTimeStamp = LocalDateTime.now()
        normalizeNewResults(game, normalizationTimeStamp)
        val newNormalizedResults = deleteOlderNormalizedResults(game)
        val mean = CalculationHelper.calculateMean(newNormalizedResults.map { it.normalizedResult!! })
        val deviation = CalculationHelper.calculateStdDeviation(newNormalizedResults.map { it.normalizedResult!! }, mean)
        return MeanAndDeviation(mean, deviation)
    }

    /**
     * For every user only 1 normalized result will remain which is the latest. All older ones are deleted.
     * @returns the list of the latest normalized results which contains 1 result per user
     */
    fun deleteOlderNormalizedResults(game: GameEntity): List<ResultForCalculationEntity>{
        val allNormalizedResults = repository.findAllByGameAndNormalizedResultNotNull(game)
        val userIdToResult = mutableMapOf<Int, ResultForCalculationEntity>()
        allNormalizedResults.forEach{result ->
            val resultInMap = userIdToResult[result.user.id]
            if(resultInMap == null){
                userIdToResult[result.user.id!!] = result
            }else  if(resultInMap.timestamp!!.isBefore(result.timestamp!!)){
                repository.delete(resultInMap)
                userIdToResult[result.user.id!!] = result
            }else{
                repository.delete(result)
            }
        }
        return userIdToResult.values.toList()
    }


    /**
     * For all not normalized result in the db calculates normalized result.
     * Deletes these not normalized results and saves only the relevant ones of the new normalized values.
     * As a result new relevant normalized values will be in database, having @param timestamp creation timestamp (default to now).
     */
    private fun normalizeNewResults(game: GameEntity, timestamp: LocalDateTime = LocalDateTime.now()){
        val resultCount = repository.countByGameAndNormalizedResultNull(game)
        val maxPages: Int = (resultCount/defaultPageSize).toInt()
        var results: List<ResultForCalculationEntity>
        var normalizedResults: List<ResultForCalculationEntity>
        for(i in 0 .. maxPages){
            results = repository.findAllByGameAndNormalizedResultNull(game, PageRequest.of(i, defaultPageSize))
            normalizedResults = ScoreCalculator.calculateNormalizedScores(results)
            repository.deleteAll(results)
            repository.saveAll(
                selectRelevantNormalizedResultsForUpdate(normalizedResults, timestamp)
            )
        }
    }

    /**
     * Selects 1 result for each user that has the maximal normalized result value.
     * All results must have normalizedResult field not null!
     * Sets timestamp to @param timestamp
      */
    private fun selectRelevantNormalizedResultsForUpdate(allNormalizedResults: List<ResultForCalculationEntity>, timestamp: LocalDateTime): List<ResultForCalculationEntity>{
        val userIdToResult = mutableMapOf<Int, ResultForCalculationEntity>()
        allNormalizedResults.forEach{ result ->
            val currentNormalizedResult = userIdToResult[result.user.id]?.normalizedResult
            if(currentNormalizedResult == null || currentNormalizedResult < result.normalizedResult!!){
                userIdToResult[result.user.id!!] = result.copy(timestamp = timestamp)
            }
        }
        return userIdToResult.values.toList()
    }
}