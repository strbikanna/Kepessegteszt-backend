package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.gameplayresult.GameplayResultRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RecommenderService(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var gameplayResultRepository: GameplayResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    fun getAllRecommendationToUser(username: String): List<RecommendedGameEntity>{
        val user = userRepository.findByUsername(username).orElseThrow()
        val systemRecommendedGames = recommendedGameRepository.findAllByRecommendedTo(user).filter { !it.completed }
        // TODO custom logic for recommendation
        return systemRecommendedGames
    }

}