package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RecommendedGameService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    @Transactional
    fun getAllRecommendedToUser(username: String, pageIndex: Int = 0, pageSize: Int = 10): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository
            .findAllPagedByRecommendedTo(user, PageRequest.of(pageIndex, pageSize))
            .map { convertToDto(it) }
    }

    fun getRecommendedGameConfig(id: Long): Map<String, Any> {
        val rGame = recommendedGameRepository.findById(id).orElseThrow()
        if(rGame.config.isEmpty()) throw NoSuchElementException("No config found for recommendation.")
        return rGame.config
    }

    fun addRecommendation(recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendedGame)
    }

    private fun convertToDto(recommendedGame: RecommendedGameEntity): RecommendedGameDto {
        val recommenderName = if(recommendedGame.recommender != null ) recommendedGame.recommender.firstName + " " + recommendedGame.recommender.lastName else ""
        return RecommendedGameDto(
            id = recommendedGame.id!!,
            name = recommendedGame.game.name,
            description = recommendedGame.game.description,
            thumbnail = recommendedGame.game.thumbnailPath,
            recommendationDate = recommendedGame.timestamp ?: LocalDateTime.now(),
            recommender = recommenderName
        )
    }
}