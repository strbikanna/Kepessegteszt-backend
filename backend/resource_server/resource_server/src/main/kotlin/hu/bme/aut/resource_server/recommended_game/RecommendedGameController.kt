package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class RecommendedGameController(@Autowired private var recommendedGameRepository: RecommendedGameRepository) {

    @GetMapping("/recommended_games")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGamesToUser(@RequestParam recommendedTo: UserEntity/*, @RequestParam(required = false) paging: Paging, @RequestParam(required = false) sorter: Sorter*/): List<RecommendedGameEntity> {
        return recommendedGameRepository.findAllByRecommendedTo(recommendedTo)
    }

    @PostMapping("/recommend")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun postRecommendedGameToUser(@RequestBody recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendedGame)
    }
}