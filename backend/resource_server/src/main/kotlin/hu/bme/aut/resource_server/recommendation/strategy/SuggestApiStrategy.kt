package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.error.ApiCallException
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.ProfileUpdateItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.suggest.SuggestRequestDto
import hu.bme.aut.resource_server.suggest.SuggestResponseDto
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.BusinessCritical
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
    private val abilityRepository: AbilityRepository,
    private val recommendedGameRepository: RecommendedGameRepository,
) : RecommendationStrategy {

    @Value("\${app.suggest-api}")
    private lateinit var SUGGEST_API_BASE_URL: String

    private lateinit var webclient: WebClient

    private var log: Logger = LoggerFactory.getLogger(SuggestApiStrategy::class.java)

    @PostConstruct
    fun initWebClient() {
        webclient = WebClient.create(SUGGEST_API_BASE_URL)
    }

    @BusinessCritical
    override suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        recommendedGameId: Long,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        val user = userRepository.findByUsernameWithProfile(username).orElseThrow()
        val game = gameRepository.findByIdWithAbilities(gameId).orElseThrow()
        if (game.modelId == null) {
            log.trace("Game with id $gameId has no modelId set for suggest-api")
            return@withContext emptyMap()
        }
        log.trace("Generating recommendation for user: $username, game: $gameId, result: $isResultSuccess")
        val suggestResponse =
            getSuggestedConfigForGame(user.profileFloat, game.modelId!!, previousConfig, isResultSuccess)
        saveAbilityUpdates(suggestResponse, recommendedGameId)
        return@withContext suggestResponse.config
    }

    @BusinessCritical
    suspend fun getSuggestedConfigForGame(
        playerAbilities: Set<FloatProfileItem>,
        gameId: String,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
    ): SuggestResponseDto {
        val allAbilities = abilityRepository.findAll()
            .filter { it.modelIndex != null }
            .sortedBy { it.modelIndex }
        val relevantAbilityValuesOrdered = allAbilities.map { ability ->
            val playerAbility = playerAbilities.find { it.ability.code == ability.code }
            playerAbility?.abilityValue ?: 0.0
        }

        val requestDto = SuggestRequestDto(
            abilities = relevantAbilityValuesOrdered,
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

            response ?: throw ApiCallException("Suggest API returned null response")
        }
        return nextConfig.await()
    }

    @BusinessCritical
    private fun saveAbilityUpdates(suggestResponseDto: SuggestResponseDto, recommendedGameId: Long) {
        val recommendedGameEntity = recommendedGameRepository.findByIdWithProfileUpdateItems(recommendedGameId).orElseThrow()
        val profileUpdateItems = mutableSetOf<ProfileUpdateItem>()
        suggestResponseDto.failureAbilities.forEachIndexed { index, value ->
            val ability = abilityRepository.findByModelIndex(index)
            ability.ifPresent { ab ->
                profileUpdateItems.add(
                    ProfileUpdateItem(
                        ability = ab,
                        updatedValue = value,
                        validOnSuccess = false
                    )
                )
            }
        }
        suggestResponseDto.successAbilities.forEachIndexed { index, value ->
            val ability = abilityRepository.findByModelIndex(index)
            ability.ifPresent {
                profileUpdateItems.add(
                    ProfileUpdateItem(
                        ability = it,
                        updatedValue = value,
                        validOnSuccess = true
                    )
                )
            }
        }
        recommendedGameEntity.profileUpdateItems = profileUpdateItems
        recommendedGameRepository.save(recommendedGameEntity)
    }
}