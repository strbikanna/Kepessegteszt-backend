package hu.bme.aut.resource_server.game

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class GameController( @Autowired private var gameRepository: GameRepository) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getAllGames(): List<GameEntity> {
        return gameRepository.findAll().toList()
    }

    @GetMapping("/{game_id}")
    @ResponseStatus(HttpStatus.OK)
    fun getGameById(@PathVariable game_id: Int): GameEntity {
        return gameRepository.findById(game_id).orElseThrow()
    }

    @PutMapping("/{game_id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun updateGame(@RequestBody gameEntity: GameEntity, @PathVariable game_id: Int): GameEntity {
        if(game_id == gameEntity.id) {
            return gameRepository.save(gameEntity)
        } else{
            throw IllegalArgumentException("Game with given ID does not exist.")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun createGame(@RequestBody gameEntity: GameEntity): GameEntity {
        return gameRepository.save(gameEntity)
    }

    @DeleteMapping("/{game_id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteGame(@RequestBody gameEntity: GameEntity, @PathVariable game_id: Int) {
        if(game_id == gameEntity.id) {
            gameRepository.deleteById(game_id)
        } else{
            throw IllegalArgumentException("Game with given ID does not exist.")
        }
    }
}
