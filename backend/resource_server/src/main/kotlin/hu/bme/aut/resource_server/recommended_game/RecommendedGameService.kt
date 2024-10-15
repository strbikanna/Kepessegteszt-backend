package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class RecommendedGameService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository,
    @Autowired private var gameRepository: GameRepository
    ) {
    /**
     * Get all recommendations to user which are not yet completed.
     */
    @Transactional
    fun getAllRecommendedToUser(username: String, pageIndex: Int = 0, pageSize: Int = 100): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository
            .findAllPagedByRecommendedToAndCompleted(user, false, PageRequest.of(pageIndex, pageSize))
            .filter { it.game.active }
            .map { it.toDto() }
    }

    @Transactional
    fun getNextChoiceForUser(username: String): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val latestCompleted = recommendedGameRepository.findLatestCompleted(user).subList(0, 2)
        return listOf(
            recommendedGameRepository.findAllByRecommendedToAndGameAndCompleted(user, latestCompleted[0].game, false).first().toDto(),
            recommendedGameRepository.findAllByRecommendedToAndGameAndCompleted(user, latestCompleted[1].game, false).first().toDto()
        )
    }

    /**
     * Retrieve the configuration of a recommended game. If the configuration is not yet available, it waits for it to be available.
     */
    suspend fun getRecommendedGameConfig(recommendedGameId: Long): Map<String, Any> = withContext(Dispatchers.IO){
        var rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        repeat(10){
            if(rGame.config.isNotEmpty()){
                return@withContext rGame.config
            }
            delay(300)
            rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        }
        throw NoSuchElementException("No config found for recommendation.")
    }

    fun addRecommendation(recommendation: RecommendationDto, recommenderUsername: String): RecommendedGameEntity {
        val recommender = userRepository.findByUsername(recommenderUsername).orElseThrow()
        val recommendedTo = userRepository.findByUsername(recommendation.recommendedTo).orElseThrow { NoSuchElementException("User with username ${recommendation.recommendedTo} not found.") }
        val game = gameRepository.findById(recommendation.gameId).orElseThrow{ NoSuchElementException("Game with id ${recommendation.gameId} not found.") }
        return recommendedGameRepository.save(RecommendedGameEntity(
            game = game,
            recommendedTo = recommendedTo,
            recommender = recommender,
            config = recommendation.config
        ))
    }

    fun getRecommendationsToUserAndGame(username: String, recommenderUsername: String, gameId: Int): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val recommender = userRepository.findByUsername(recommenderUsername).orElseThrow()
        val game = gameRepository.findById(gameId).orElseThrow()
        return recommendedGameRepository.findByRecommendedToAndGameAndRecommender(user, game, recommender).map { it.toDto() }
    }
}