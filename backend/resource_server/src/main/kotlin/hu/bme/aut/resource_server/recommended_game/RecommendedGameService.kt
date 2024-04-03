package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class RecommendedGameService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    @Transactional
    fun getAllRecommendedToUser(username: String, pageIndex: Int = 0, pageSize: Int = 10): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository.findAllPagedByRecommendedTo(user, PageRequest.of(pageIndex, pageSize))
    }

    fun addRecommendation(recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendedGame)
    }
}