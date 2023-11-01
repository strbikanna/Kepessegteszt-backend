package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Entity
@Table(name = "RECOMMENDED_GAME")
data class RecommendedGameEntity(
    @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,

    @CreationTimestamp
        @Column(name = "_timestamp")
        val timestamp: LocalDateTime? = null,

    @Column(name = "_level")
        val level: Int = 0,

    @Type(JsonType::class)
        val config: Map<String, Any>,

    var completed: Boolean = false,

    @JoinColumn(name = "recommender_id")
        @ManyToOne(fetch = FetchType.EAGER)
        val recommender: UserEntity? = null,

    @ManyToOne
        @JoinColumn(name = "recommendee_id")
        val recommendedTo: UserEntity,

    @ManyToOne
        @JoinColumn(name = "game_id")
        val game: GameEntity
)