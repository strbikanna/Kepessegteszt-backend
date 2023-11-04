package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.ability.AbilityEntity
import jakarta.persistence.*
import org.hibernate.annotations.Type
import io.hypersistence.utils.hibernate.type.json.JsonType

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

    val thumbnailPath: String,

    @Column(name ="_active")
    val active: Boolean,

    val url: String?,

    @Type(JsonType::class)
    val configDescription: MutableMap<String, Any>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "GAME_ABILITIES",
        joinColumns = arrayOf(JoinColumn(name = "game_id")),
        inverseJoinColumns = arrayOf(JoinColumn(name = "ability_code"))
    )
    val affectedAbilites: MutableSet<AbilityEntity>
)