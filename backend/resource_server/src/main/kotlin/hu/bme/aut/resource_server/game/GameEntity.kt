package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.ability.AbilityEntity
import jakarta.persistence.*
import org.hibernate.annotations.Type
import io.hypersistence.utils.hibernate.type.json.JsonType

/**
 * Entity class that represents games.
 */
@Entity
@Table(name = "game")
data class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var version: Int,

    @Column(name ="_name")
    val name: String,

    @Column(name ="_description")
    val description: String,

    @JsonProperty("thumbnail")
    val thumbnailPath: String,

    @Column(name ="_active")
    var active: Boolean,

    val url: String?,

    @Type(JsonType::class)
    val configDescription: MutableMap<String, Any>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "GAME_ABILITIES",
        joinColumns = arrayOf(JoinColumn(name = "game_id")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "ability_code"))
    )
    val affectedAbilities: MutableSet<AbilityEntity>
)
