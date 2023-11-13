package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.user.UserEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type
import java.time.LocalDateTime

@Entity(name = "result_for_calculation")
data class ResultForCalculationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreationTimestamp
    @Column(name = "_timestamp")
    val timestamp: LocalDateTime? = null,

    @Type(JsonType::class)
    val result: Map<String, Any?>,

    var normalizedResult: Double? = null,

    @Type(JsonType::class)
    val config: MutableMap<String, Any>,

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "game_id")
    val game: GameEntity,
)