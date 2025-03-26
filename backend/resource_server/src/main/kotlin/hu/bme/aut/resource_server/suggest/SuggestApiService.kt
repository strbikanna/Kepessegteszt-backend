package hu.bme.aut.resource_server.suggest

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.error.ApiCallException
import hu.bme.aut.resource_server.profile.FloatProfileItem
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class SuggestApiService {

    @Value("\${app.suggest-api}")
    private lateinit var SUGGEST_API_BASE_URL: String

    private lateinit var webclient: WebClient

    @PostConstruct
    fun initWebClient() {
        webclient = WebClient.create(SUGGEST_API_BASE_URL)
    }

    suspend fun getSuggestedConfigForGame(
        playerAbilities: Set<FloatProfileItem>,
        gameAbilities: Set<AbilityEntity>,
        gameId: Int,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
        abilityAccuracy: Double = 0.0
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

            response?.config ?: throw ApiCallException("Next config is missing")

        }
        return nextConfig.await()
    }
}