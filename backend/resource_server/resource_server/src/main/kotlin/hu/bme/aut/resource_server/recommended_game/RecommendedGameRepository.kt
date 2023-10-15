package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface RecommendedGameRepository: CrudRepository<RecommendedGameEntity, Long>, PagingAndSortingRepository<RecommendedGameEntity, Long> {
    fun findAllByRecommendedTo(recommendedTo: UserEntity): List<RecommendedGameEntity>
}