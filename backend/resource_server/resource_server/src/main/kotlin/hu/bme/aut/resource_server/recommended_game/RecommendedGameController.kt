package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


@RestController
@RequestMapping("/game")
class RecommendedGameController(@Autowired private var recommendedGameRepository: RecommendedGameRepository) {

    @GetMapping("/recommended_games")
    @ResponseStatus(HttpStatus.OK)
    fun getRecommendedGamesToUser(@RequestParam recommendedTo: UserEntity, @RequestParam(required = false) page: Pageable, @RequestParam(required = false) sort: Sort): List<RecommendedGameEntity> {
        if (page.equals(null) && sort.equals(null))
            return recommendedGameRepository.findAllByRecommendedTo(recommendedTo)
        else if (sort.equals(null))
            return recommendedGameRepository.findAllPagedByRecommendedTo(recommendedTo, page)
        else (page.equals(null))
            return recommendedGameRepository.findAllSortedByRecommendedTo(recommendedTo, sort)
    }

    @PostMapping("/recommend")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST') or hasRole('TEACHER')")
    fun postRecommendedGameToUser(@RequestBody recommendedGame: RecommendedGameEntity): RecommendedGameEntity {
        return recommendedGameRepository.save(recommendedGame)
    }
}