package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem

data class GameDto(
    val id: Int?,

    val version: Int,

    val name: String,

    val description: String,

    @JsonProperty("thumbnail")
    val thumbnailPath: String,

    val active: Boolean,

    val affectedAbilities: MutableSet<AbilityEntity>,

    val configItems: MutableSet<ConfigItem> = mutableSetOf(),

    val storedConfig: Boolean = false
) {
    constructor(gameEntity: GameEntity) : this(
        id = gameEntity.id,
        version = gameEntity.version,
        name = gameEntity.name,
        description = gameEntity.description,
        thumbnailPath = gameEntity.thumbnailPath,
        active = gameEntity.active,
        affectedAbilities = gameEntity.affectedAbilities,
        configItems = gameEntity.configItems,
        storedConfig = gameEntity is StoredConfigGameEntity
    ) {   }

    fun toGameEntity(): GameEntity {
        return if (storedConfig)
            StoredConfigGameEntity(
                id = id,
                version = version,
                name = name,
                description = description,
                thumbnailPath = thumbnailPath,
                active = active,
                affectedAbilities = affectedAbilities,
                configItems = configItems
            ) else
            GameEntity(
            id = id,
            version = version,
            name = name,
            description = description,
            thumbnailPath = thumbnailPath,
            active = active,
            affectedAbilities = affectedAbilities,
            configItems = configItems
        )
    }

}