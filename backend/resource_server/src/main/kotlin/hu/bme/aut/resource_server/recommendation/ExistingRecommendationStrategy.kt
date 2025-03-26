package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class ExistingRecommendationStrategy(
    private var recommendedGameRepository: RecommendedGameRepository,
    private var gameRepository: GameRepository,
    private var userRepository: UserRepository
) : RecommendationStrategy {


    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val game = gameRepository.findById(gameId).orElseThrow()
        val user = userRepository.findByUsername(username).orElseThrow()
        return@withContext recommendedGameRepository
            .findByRecommendedToAndGameAndCompletedAndRecommender(user, game, false, null)
            .firstOrNull()?.config ?: emptyMap()

    }
}