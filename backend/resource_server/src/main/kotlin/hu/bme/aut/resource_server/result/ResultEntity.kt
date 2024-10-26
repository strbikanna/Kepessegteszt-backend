package hu.bme.aut.resource_server.result

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

/**
 * Entity that is immutable and represents a record of a gameplay result.
 */
@Entity
@Table(name = "GAMEPLAY")
data class ResultEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreationTimestamp
    @Column(name = "_timestamp")
    val timestamp: LocalDateTime? = null,

    @Type(JsonType::class)
    val result: Map<String, Any>,

    @Column(name = "passed")
    val passed: Boolean? = null,

    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @Type(JsonType::class)
    val config: MutableMap<String, Any>,

    @JsonIgnore
    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "recommended_game_id")
    val recommendedGame: RecommendedGameEntity,
) {
}
