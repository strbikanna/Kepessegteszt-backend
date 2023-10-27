package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class GameplayService(
    @Autowired private var gameplayRepository: GameplayRepository,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var userRepository: UserRepository

) {
    fun save(data: GameplayDto){
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
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: GameplayDto){
        val user = authentication.authorities.find{it.authority == gameplay.username}
        val game = gameRepository.findById(gameplay.gameId)
        if(game.isEmpty || user == null || Integer.parseInt(authentication.name) != game.get().id){
            throw IllegalAccessException("This game is not authorized to save gameplay for this user.")
        }
    }

}