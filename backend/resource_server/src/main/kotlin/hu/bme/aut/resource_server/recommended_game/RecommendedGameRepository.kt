package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime
import java.util.*

interface RecommendedGameRepository: JpaRepository<RecommendedGameEntity, Long> {
    fun findAllByRecommendedTo(recommendedTo: UserEntity): List<RecommendedGameEntity>
    fun findAllByRecommender(recommender: UserEntity): List<RecommendedGameEntity>
    fun findAllByRecommendedToAndGameAndCompleted(recommendedTo: UserEntity, game:GameEntity, completed: Boolean): List<RecommendedGameEntity>
    fun findAllByRecommendedToAndCompleted(recommendedTo: UserEntity, completed: Boolean): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedToAndCompleted(recommendedTo: UserEntity, completed: Boolean, page: Pageable): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedToAndCompletedAndGameIn(recommendedTo: UserEntity, completed: Boolean, games: List<GameEntity>, page: Pageable): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedToAndCompletedAndGame(recommendedTo: UserEntity, completed: Boolean, game:GameEntity, page: Pageable): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedToAndGame(recommendedTo: UserEntity, game:GameEntity, page: Pageable): List<RecommendedGameEntity>
    fun findAllPagedByRecommendedTo(recommendedTo: UserEntity, page: Pageable): List<RecommendedGameEntity>
    fun findAllSortedByRecommendedTo(recommendedTo: UserEntity, sort: Sort): List<RecommendedGameEntity>
    fun findByRecommendedToAndGameAndCompletedAndRecommender(user: UserEntity, game: GameEntity, completed: Boolean, recommender: UserEntity?): List<RecommendedGameEntity>

    @Query(
        "select rg from RecommendedGameEntity rg where rg.recommendedTo = :user and rg.completed = true " +
                "and rg.game.active = true and rg.timestamp = (" +
                "select max(rg2.timestamp) from RecommendedGameEntity rg2 where rg2.game = rg.game and rg2.recommendedTo = :user and rg2.completed = true and rg2.game.active = true" +
                ") order by rg.timestamp asc"
    )
    fun findLatestCompleted(user: UserEntity): List<RecommendedGameEntity>

    @Query(
        "select rg from RecommendedGameEntity  rg " +
                "where (select count(rg2) from RecommendedGameEntity rg2 " +
                "where rg2.game = rg.game and rg2.recommendedTo = rg.recommendedTo and rg2.completed = true) = 0 " +
                "and rg.recommendedTo = :user"
    )
    fun findByRecommendedToAndNeverPlayed(user: UserEntity) : List<RecommendedGameEntity>

    @Query(
        "select rg from RecommendedGameEntity rg " +
                "left join fetch rg.profileUpdateItems pui " +
                "where rg.id = :id"
    )
    fun findByIdWithProfileUpdateItems(id: Long): Optional<RecommendedGameEntity>

    fun deleteByGameAndCompletedIsFalse(game: GameEntity)
}