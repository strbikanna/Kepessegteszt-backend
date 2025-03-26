package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommendation.StoredRecommendationService
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("stored_config")
class StoredConfigGameEntity(
    id: Int? = null,
    version: Int,
    name: String,
    description: String,
    thumbnailPath: String,
    active: Boolean,
    affectedAbilities: MutableSet<AbilityEntity> = mutableSetOf(),
    configItems: MutableSet<ConfigItem> = mutableSetOf(),
) : GameEntity(id, version, name, description, thumbnailPath, active, affectedAbilities, configItems) {

    override fun validateConfig(config: Map<String, Any>): Map<String, Any> {
        val storedConfigId = config.values.first() as Int
        return try{
            StoredRecommendationService.getStoredRecommendationById(storedConfigId)
        }catch (e: Exception){
            throw IllegalArgumentException("Stored recommendation not found with id: $id")
        }
    }
}
