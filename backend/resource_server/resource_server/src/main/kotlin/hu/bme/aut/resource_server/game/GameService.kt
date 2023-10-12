package hu.bme.aut.resource_server.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GameService(
        @Autowired private var gameRepository: GameRepository,
){

    fun getAllGames(): List<GameDto>{
        return gameRepository.findAll().map { GameDto(it) }
    }

    fun addGame(game: GameEntity) {
        gameRepository.save(game);
    }
    fun updateGame(game: GameEntity){
        val gameEntity = gameRepository.findGameByName(game.name).orElseThrow()
        gameRepository.updateGameData(game.name, game.description, game.icon, game.active, game.url, game.jsonDescriptor, game.id!!)
    }

    fun getGameById(id: Int) : GameEntity{
        return gameRepository.findGameById(id).orElseThrow()
    }

    fun getGameByName(name: String) : GameEntity{
        return gameRepository.findGameByName(name).orElseThrow()
    }

}