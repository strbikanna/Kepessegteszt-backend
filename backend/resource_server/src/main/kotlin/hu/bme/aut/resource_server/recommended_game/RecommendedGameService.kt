package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RecommendedGameService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository,
    ) {
    /**
     * Get all recommendations to user which are not yet completed.
     */
    @Transactional
    fun getAllRecommendedToUser(username: String, pageIndex: Int = 0, pageSize: Int = 10): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository
            .findAllPagedByRecommendedToAndCompleted(user, false, PageRequest.of(pageIndex, pageSize))
            .filter { it.game.active }
            .map { convertToDto(it) }
    }

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

    fun addRecommendation(recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendedGame)
    }

    private fun convertToDto(recommendedGame: RecommendedGameEntity): RecommendedGameDto {
        val recommenderName = if(recommendedGame.recommender != null ) recommendedGame.recommender.firstName + " " + recommendedGame.recommender.lastName else ""
        return RecommendedGameDto(
            id = recommendedGame.id!!,
            gameId = recommendedGame.game.id!!,
            name = recommendedGame.game.name,
            description = recommendedGame.game.description,
            thumbnail = recommendedGame.game.thumbnailPath,
            recommendationDate = recommendedGame.timestamp ?: LocalDateTime.now(),
            recommender = recommenderName
        )
    }
}