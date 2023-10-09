package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.Game
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "RECOMMENDED_GAME")
data class RecommendedGame(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreationTimestamp
    @Column(name = "_timestamp")
    val timestamp: LocalDateTime? = null,

    @JoinColumn(name = "recommender_id")
    @ManyToOne(fetch = FetchType.EAGER)
    val recommender: UserEntity,

    @ManyToOne
    @JoinColumn(name = "recommendee_id")
    val recommendedTo: UserEntity,

    @ManyToOne
    @JoinColumn(name = "game_id")
    val game: Game
)