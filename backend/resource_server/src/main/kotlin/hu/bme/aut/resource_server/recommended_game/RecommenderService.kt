package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommendation.AutoRecommendationService
import hu.bme.aut.resource_server.user.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class RecommenderService(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var autoRecommender: AutoRecommendationService,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    val log: Logger = LoggerFactory.getLogger(RecommenderService::class.java)
    fun getAllRecommendationToUser(username: String): List<RecommendedGameEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return recommendedGameRepository.findAllByRecommendedToAndCompletedAndRecommender(user, false, null)
    }

    fun createNewRecommendations(username: String): List<RecommendedGameEntity>{
        val games = gameRepository.findAll()
        val user = userRepository.findByUsername(username).orElseThrow()
        val recommendations = mutableListOf<RecommendedGameEntity>()
        try{
            games.forEach {game ->
                recommendations.add(autoRecommender.generateRecommendationForUser(user, game))
            }
            recommendedGameRepository.saveAll(recommendations)
        }catch(e: RuntimeException){
            log.error("Error while generating recommendation for user $username", e)
            return recommendations
        }
      return recommendations
    }

    fun deleteRecommendations(recommendations: List<RecommendedGameEntity>){
        recommendedGameRepository.deleteAll(recommendations)
    }

}