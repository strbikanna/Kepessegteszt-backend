package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GamePlayRecommenderService(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var gameplayRepository: GameplayRepository,
    @Autowired private var userRepository: UserRepository
) {
    fun getAllRecommendationToUser(username: String): List<GameplayDto>{
        val user = userRepository.findByUsername(username).orElseThrow()
        val games = gameRepository.findAll().filter { it.active }
        //TODO custom logic for recommendation
        return games.map { game -> convertToDto(game, user)}
    }
    private fun convertToDto(gameEntity: GameEntity, userEntity: UserEntity): GameplayDto {
        gameEntity.configDescription["game_id"] = gameEntity.id!!
        gameEntity.configDescription["username"] = userEntity.username
        return GameplayDto(
            id = gameEntity.id,
            name = gameEntity.name,
            description = gameEntity.description,
            thumbnail = gameEntity.thumbnailPath,
            url = gameEntity.url,
            config = gameEntity.configDescription
        )
    }

}