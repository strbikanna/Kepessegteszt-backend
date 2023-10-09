package hu.bme.aut.resource_server.gameplay

import com.fasterxml.jackson.databind.JsonNode
import hu.bme.aut.resource_server.game.Game
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Entity
data class GamePlay(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreationTimestamp
    @Column(name = "_timestamp")
    val timestamp: LocalDateTime? = null,

    @Type(JsonType::class)
    val result: JsonNode,

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "game_id")
    val game: Game
) {
}