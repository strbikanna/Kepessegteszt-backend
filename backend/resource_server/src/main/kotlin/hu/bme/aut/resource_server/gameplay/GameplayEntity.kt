package hu.bme.aut.resource_server.gameplay

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Entity
@Table(name = "GAMEPLAY")
data class GameplayEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreationTimestamp
    @Column(name = "_timestamp")
    val timestamp: LocalDateTime? = null,

    @Type(JsonType::class)
    val result: Map<String, Any?>,

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    val user: UserEntity,

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "game_id")
    val game: GameEntity
)