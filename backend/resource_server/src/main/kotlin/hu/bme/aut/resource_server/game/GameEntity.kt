package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.utils.BusinessCritical
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "game_type")
@DiscriminatorValue("default")
open class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null,

    /**
     * Unique identifier of the game model used in the suggest params api.
     */
    open val modelId: String? = null,

    open var version: Int,

    @Column(name ="_name")
    open var name: String,

    @Column(name ="_description")
    open val description: String,

    @JsonProperty("thumbnail")
    @Column(name ="thumbnail_path")
    open var thumbnailPath: String,

    @Column(name ="_active")
    open var active: Boolean,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "GAME_ABILITIES",
        joinColumns = [JoinColumn(name = "game_id")],
        inverseJoinColumns = [JoinColumn(name = "ability_code")]
    )
    open val affectedAbilities: MutableSet<AbilityEntity>,

    @OneToMany
    @JoinColumn(name = "game_id")
    @Cascade(CascadeType.ALL)
    open var configItems: MutableSet<ConfigItem> = mutableSetOf()
){
    @BusinessCritical
    open fun validateConfig(config: Map<String, Any>): Map<String, Any>{
        return config
    }
}