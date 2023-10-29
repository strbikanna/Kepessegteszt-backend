package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class GameplayService(
    @Autowired private var gameplayRepository: GameplayRepository,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var userRepository: UserRepository

) {
    fun save(data: GameplayResultDto){
        val game = gameRepository.findById(data.gameId).orElseThrow()
        val user = userRepository.findByUsername(data.username).orElseThrow()
        val gameplay = GameplayEntity(
            result = data.gameResult,
            user = user,
            game = game
        )
        gameplayRepository.save(gameplay)
    }

    fun getAllByUser(username: String): List<GameplayEntity> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return gameplayRepository.findAllByUser(user)
    }
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: GameplayResultDto){
        val username = authentication.name
        val game = gameRepository.findById(gameplay.gameId)
        val jwt = authentication.principal as Jwt
        val tokenGameId = jwt.claims["game_id"] as String?
        if(game.isEmpty || username == null || Integer.parseInt(tokenGameId) != game.get().id){
            throw IllegalAccessException("This game is not authorized to save gameplay for this user.")
        }
    }

}