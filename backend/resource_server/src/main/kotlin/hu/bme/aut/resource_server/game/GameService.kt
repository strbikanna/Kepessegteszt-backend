package hu.bme.aut.resource_server.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class GameService (
    @Autowired private var gameRepository: GameRepository) {
    fun getAllGames(): List<GameDto> {
        return gameRepository.findAll().map { GameDto(it) }
    }

    fun getGameById(id: Int): Optional<GameDto> {
        return gameRepository.findById(id).map { GameDto(it) }
    }

}