package hu.bme.aut.resource_server.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class GameController(@Autowired private var gameService: GameService) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(id: Int): List<GameDto> {
        return gameService.getAllGames()
    }

}