package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.error.ApiCallException
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.suggest.SuggestRequestDto
import hu.bme.aut.resource_server.suggest.SuggestResponseDto
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SuggestApiStrategy(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
) : RecommendationStrategy {

    @Value("\${app.suggest-api}")
    private lateinit var SUGGEST_API_BASE_URL: String

    private lateinit var webclient: WebClient

    private var log: Logger = LoggerFactory.getLogger(SuggestApiStrategy::class.java)

    @PostConstruct
    fun initWebClient() {
        webclient = WebClient.create(SUGGEST_API_BASE_URL)
    }

    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsernameWithProfile(username).orElseThrow()
        val game = gameRepository.findByIdWithAbilities(gameId).orElseThrow()
        if(game.modelId == null){
            log.trace("Game with id $gameId has no modelId set for suggest-api")
            return@withContext emptyMap()
        }
        log.trace("Generating recommendation for user: $username, game: $gameId, result: $isResultSuccess")
        return@withContext getSuggestedConfigForGame(user.profileFloat, game.affectedAbilities, game.modelId!!, previousConfig, isResultSuccess)
    }

    suspend fun getSuggestedConfigForGame(
        playerAbilities: Set<FloatProfileItem>,
        gameAbilities: Set<AbilityEntity>,
        gameId: String,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
    ): Map<String, Any> {
        val relevantAbilitiesOrdered = playerAbilities
            .filter { gameAbilities.contains(it.ability) }
            .sortedBy { it.ability.code }

        val requestDto = SuggestRequestDto(
            abilities = relevantAbilitiesOrdered.map { it.abilityValue },
            previousParams = previousConfig.map { it.key to it.value as Int }.toMap(),
            resultSuccess = isResultSuccess,
        )

        val nextConfig = CoroutineScope(Dispatchers.IO).async {
            val response = try {
                webclient.post()
                    .uri("/games/$gameId/suggest")
                    .bodyValue(requestDto)
                    .retrieve()
                    .bodyToMono(SuggestResponseDto::class.java)
                    .block()
            } catch (e: Exception) {
                throw ApiCallException("Failed to get suggestion from suggest-api", e)
            }

            log.trace("Suggest response: {}", response)

            response?.config ?: throw ApiCallException("Next config is missing")

        }
        return nextConfig.await()
    }
}