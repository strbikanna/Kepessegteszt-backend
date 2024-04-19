package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface RecommendedGameRepository: CrudRepository<RecommendedGameEntity, Long>, PagingAndSortingRepository<RecommendedGameEntity, Long> {
    fun findAllByRecommendedTo(recommendedTo: UserEntity): List<RecommendedGameEntity>
    fun findAllByRecommendedToAndCompleted(recommendedTo: UserEntity, completed: Boolean): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedToAndCompleted(recommendedTo: UserEntity, completed: Boolean, page: Pageable): List<RecommendedGameEntity>
    fun findAllByRecommendedToAndRecommender(recommendedTo: UserEntity, recommendedBy: UserEntity): List<RecommendedGameEntity>
    fun findAllByRecommendedToAndCompletedAndRecommender(recommendedTo: UserEntity, completed: Boolean, recommendedBy: UserEntity?): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedTo(recommendedTo: UserEntity, page: Pageable): List<RecommendedGameEntity>
    fun findAllSortedByRecommendedTo(recommendedTo: UserEntity, sort: Sort): List<RecommendedGameEntity>
    fun findTopByTimestampBeforeAndRecommendedToAndGameOrderByTimestamp(timestamp: LocalDateTime, recommendedTo: UserEntity, game: GameEntity): RecommendedGameEntity?
}