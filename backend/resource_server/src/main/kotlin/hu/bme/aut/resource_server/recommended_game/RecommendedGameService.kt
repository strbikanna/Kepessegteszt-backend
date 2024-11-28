package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        val sort = Sort.by(Sort.Order.desc("timestamp"))
        return recommendedGameRepository
            .findAllPagedByRecommendedToAndCompleted(user, false, PageRequest.of(pageIndex, pageSize, sort))
            .filter { it.game.active }
            .map { it.toDto() }
    }

    @Transactional
    fun getNextChoiceForUser(username: String): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val latestCompleted = recommendedGameRepository.findLatestCompleted(user)
        val top2Distinct: MutableList<GameEntity> = mutableListOf()
        latestCompleted.forEach {
            if (top2Distinct.size < 2 && !top2Distinct.contains(it.game)) {
                top2Distinct.add(it.game)
            }
        }
        return top2Distinct.map { game ->
            recommendedGameRepository.findAllByRecommendedToAndGameAndCompleted(
                user,
                game,
                false
            ).first().toDto()
        }
    }

    /**
     * Retrieve the configuration of a recommended game. If the configuration is not yet available, it waits for it to be available.
     */
    suspend fun getRecommendedGameConfig(recommendedGameId: Long): Map<String, Any> = withContext(Dispatchers.IO) {
        var rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        repeat(10) {
            if (rGame.config.isNotEmpty()) {
                return@withContext rGame.config
            }
            delay(300)
            rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        }
        throw NoSuchElementException("No config found for recommendation.")
    }

    fun addRecommendation(recommendation: RecommendationDto, recommenderUsername: String): RecommendedGameEntity {
        val recommender = userRepository.findByUsername(recommenderUsername).orElseThrow()
        val recommendedTo = userRepository.findByUsername(recommendation.recommendedTo)
            .orElseThrow { NoSuchElementException("User with username ${recommendation.recommendedTo} not found.") }
        val game = gameRepository.findById(recommendation.gameId)
            .orElseThrow { NoSuchElementException("Game with id ${recommendation.gameId} not found.") }
        return recommendedGameRepository.save(
            RecommendedGameEntity(
                game = game,
                recommendedTo = recommendedTo,
                recommender = recommender,
                config = recommendation.config
            )
        )
    }

    fun getRecommendationsToUserAndGame(username: String, gameId: Int?, completed: Boolean?): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val page = PageRequest.of(0, 100, Sort.by("timestamp").descending())
        if (gameId == null) {
            return if (completed == null) {
                recommendedGameRepository.findAllPagedByRecommendedTo(user, page).map { it.toDto() }
            } else {
                recommendedGameRepository.findAllPagedByRecommendedToAndCompleted(user, completed, page)
                    .map { it.toDto() }
            }
        }
        val game = gameRepository.findById(gameId).orElseThrow()
        return if (completed == null) {
            recommendedGameRepository.findAllPagedByRecommendedToAndGame(user, game, page).map { it.toDto() }
        } else {
            recommendedGameRepository.findAllPagedByRecommendedToAndCompletedAndGame(user, completed, game, page)
                .map { it.toDto() }
        }
    }

    fun deleteRecommendedGame(recommendedGameId: Long) {
        val rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        if(rGame.completed) {
            throw IllegalArgumentException("Cannot delete completed recommendation, because it is completed already.")
        }
        recommendedGameRepository.deleteById(recommendedGameId)
    }
}