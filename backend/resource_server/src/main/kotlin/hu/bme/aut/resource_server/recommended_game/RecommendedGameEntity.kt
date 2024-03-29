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
@Table(name = "recommended_game")
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
        val config: Map<String, Any>,

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
