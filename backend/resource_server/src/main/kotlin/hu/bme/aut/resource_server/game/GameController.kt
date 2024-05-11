package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.recommended_game.RecommenderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/game")
class GameController(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var gameService: GameService,
    @Autowired private var recommenderService: RecommenderService
) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getAllGames(
        @RequestParam(required = false, defaultValue = "0") pageIndex: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int
    ): List<GameEntity> {
        return gameService.getAllGamesPaged(pageIndex, pageSize)
    }

    @GetMapping("/all/count")
    @ResponseStatus(HttpStatus.OK)
    fun getAllGamesCount(): Long {
        return gameRepository.count()
    }

    @GetMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    fun getGameById(@PathVariable gameId: Int): GameEntity {
        return gameService.getGameById(gameId).orElseThrow()
    }


    @PutMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SCIENTIST')")
    fun updateGame(@RequestBody gameEntity: GameEntity, @PathVariable gameId: Int): GameEntity {
        if(gameId == gameEntity.id) {
            val updated = gameService.updateGame(gameEntity)
            if(updated.id != gameId){
                recommenderService.createDefaultRecommendationsForGame(updated.id!!)
            }
            return updated
        } else{
            throw IllegalArgumentException("Game IDs don't match.")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun createGame(@RequestBody gameEntity: GameEntity): GameEntity {
        val game = gameRepository.save(gameEntity)
        recommenderService.createDefaultRecommendationsForGame(game.id!!)
        return game
    }

    /**
     * Endpoint to upload a (new) thumbnail for a game.
     */
    @PostMapping("/image/{gameId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun uploadThumbnail(@PathVariable gameId: Int, @RequestParam file: MultipartFile): GameEntity{
        return this.gameService.saveThumbnailForGame(gameId, file)
    }

    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteGame(@PathVariable gameId: Int) {
        if(gameRepository.existsById(gameId)) {
            gameRepository.deleteById(gameId)
        } else{
            throw IllegalArgumentException("Game ids do not match.")
        }
    }
}
