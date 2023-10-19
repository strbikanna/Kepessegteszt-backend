package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort


interface RecommendedGameRepository: CrudRepository<RecommendedGameEntity, Long>, PagingAndSortingRepository<RecommendedGameEntity, Long> {
    fun findAllByRecommendedTo(recommendedTo: UserEntity): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedTo(recommendedTo: UserEntity, page: Pageable): List<RecommendedGameEntity>
    fun findAllSortedByRecommendedTo(recommendedTo: UserEntity, sort: Sort): List<RecommendedGameEntity>
}