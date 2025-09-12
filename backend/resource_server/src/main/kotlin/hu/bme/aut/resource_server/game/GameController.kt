package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.recommendation.RecommenderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/game")
class GameController(
    @Autowired private var gameService: GameService,
    @Autowired private var recommenderService: RecommenderService
) {

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    fun getAllGames(
        @RequestParam(required = false, defaultValue = "0") pageIndex: Int,
        @RequestParam(required = false, defaultValue = "100") pageSize: Int,
        @RequestParam(required = false) active: Boolean?
    ): List<GameDto> {
        return if(active != null) gameService.getGamesByActive(active, pageIndex, pageSize).map { GameDto(it) }
        else gameService.getAllGames(pageIndex, pageSize).map { GameDto(it) }
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    fun getAllGamesCount(
        @RequestParam(required = false) active: Boolean?
    ): Long {
        return gameService.getCountOfGames(active)
    }

    @GetMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    fun getGameById(@PathVariable gameId: Int): GameDto {
        return GameDto(
            gameService.getGameById(gameId).orElseThrow()
        )
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun getGamesByName(@RequestParam name: String): List<GameDto> {
        return gameService.getGamesByName(name).map { GameDto(it) }
    }


    @PutMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SCIENTIST')")
    fun updateGame(@RequestBody gameDto: GameDto, @PathVariable gameId: Int): GameDto {
        if(gameId == gameDto.id) {
            return GameDto(gameService.updateGame(gameDto.toGameEntity()))
        } else{
            throw IllegalArgumentException("Game IDs don't match.")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun createGame(@RequestBody gameDto: GameDto): GameDto {
        val game = gameService.saveGame(gameDto.toGameEntity())
        recommenderService.createDefaultRecommendationsForGame(game.id!!)
        return GameDto(game)
    }

    /**
     * Endpoint to upload a (new) thumbnail for a game.
     */
    @PostMapping("/image/{gameId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun uploadThumbnail(@PathVariable gameId: Int, @RequestParam file: MultipartFile): GameDto{
        return GameDto(
            this.gameService.saveThumbnailForGame(gameId, file)
        )
    }

    @DeleteMapping("/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteGame(@PathVariable gameId: Int) {
        gameService.deleteGame(gameId)
    }
}
