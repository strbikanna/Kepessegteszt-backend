package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.utils.BusinessCritical
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class DefaultRecommendationStrategy(
    private var gameRepository: GameRepository
) : RecommendationStrategy {
    @BusinessCritical
    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        recommendedGameId: Long,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val configItems = gameRepository.findByIdWithConfigItems(gameId).orElseThrow().configItems
        return@withContext configItems.associate { it.paramName to it.initialValue }
    }
}