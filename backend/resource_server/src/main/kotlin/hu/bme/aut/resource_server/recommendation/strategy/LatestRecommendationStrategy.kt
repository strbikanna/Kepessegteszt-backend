package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.BusinessCritical
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class LatestRecommendationStrategy(
    private var recommendedGameRepository: RecommendedGameRepository,
    private var userRepository: UserRepository
) : RecommendationStrategy {


    @BusinessCritical
    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        recommendedGameId: Long,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsername(username).orElseThrow()
        return@withContext recommendedGameRepository.findLatestCompleted(user)
            .find { it.game.id == gameId && it.config.isNotEmpty() }
            ?.config ?: emptyMap()

    }
}