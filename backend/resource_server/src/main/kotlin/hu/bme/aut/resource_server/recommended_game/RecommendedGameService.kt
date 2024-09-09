package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.RoleName
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

    /**
     * Retrieve the configuration of a recommended game. If the configuration is not yet available, it waits for it to be available.
     */
    suspend fun getRecommendedGameConfig(id: Long): Map<String, Any> = withContext(Dispatchers.IO){
        var rGame = recommendedGameRepository.findById(id).orElseThrow()
        repeat(10){
            if(rGame.config.isNotEmpty()){
                return@withContext rGame.config
            }
            delay(300)
            rGame = recommendedGameRepository.findById(id).orElseThrow()
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
}