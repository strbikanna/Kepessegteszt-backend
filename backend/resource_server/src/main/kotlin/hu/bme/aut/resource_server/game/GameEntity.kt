package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Cascade
import org.hibernate.annotations.CascadeType
import org.hibernate.annotations.Type

/**
 * Entity class that represents games.
 */
@Entity
@Table(name = "GAME")
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

    //TODO remove when normalization is refactored
    @Type(JsonType::class)
    val configDescription: MutableMap<String, Any>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "GAME_ABILITIES",
        joinColumns = [JoinColumn(name = "game_id")],
        inverseJoinColumns = [JoinColumn(name = "ability_code")]
    )
    val affectedAbilities: MutableSet<AbilityEntity>,

    @OneToMany
    @JoinColumn(name = "game_id")
    @Cascade(CascadeType.ALL)
    val configItems: MutableSet<ConfigItem> = mutableSetOf()
)