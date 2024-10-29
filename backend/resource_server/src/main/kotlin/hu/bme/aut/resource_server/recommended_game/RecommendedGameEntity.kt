package hu.bme.aut.resource_server.recommended_game

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

/**
 * Entity for storing recommended games.
 */
@Entity
@Table(name = "RECOMMENDED_GAME")
data class RecommendedGameEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

        @CreationTimestamp
        @Column(name = "_timestamp")
        @JsonProperty("recommendationDate")
        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        val timestamp: LocalDateTime? = null,

        @Type(JsonType::class)
        var config: Map<String, Any>,

        var completed: Boolean = false,

        @JoinColumn(name = "recommender_id")
        @ManyToOne(fetch = FetchType.EAGER)
        val recommender: UserEntity? = null,

        @JsonIgnore
        @ManyToOne
        @JoinColumn(name = "recommendee_id")
        val recommendedTo: UserEntity,

        @ManyToOne
        @JoinColumn(name = "game_id")
        val game: GameEntity
)

fun RecommendedGameEntity.toDto(): RecommendedGameDto{
        val recommenderName = if(this.recommender != null ) this.recommender.firstName + " " + this.recommender.lastName else ""
        val recommendedToName = this.recommendedTo.firstName + " " + this.recommendedTo.lastName
        return RecommendedGameDto(
                id = this.id!!,
                gameId = this.game.id!!,
                name = this.game.name,
                description = this.game.description,
                thumbnail = this.game.thumbnailPath,
                recommendationDate = this.timestamp ?: LocalDateTime.now(),
                recommender = recommenderName,
                recommendedTo = recommendedToName,
                completed = this.completed,
                config = this.config
        )
}