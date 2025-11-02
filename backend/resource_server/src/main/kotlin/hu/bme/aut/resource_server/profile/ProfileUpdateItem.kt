package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Embeddable
data class ProfileUpdateItem(
    @ManyToOne
    @JoinColumn(name="ability_id", referencedColumnName = "code")
    val ability: AbilityEntity,

    val updatedValue: Double,

    val validOnSuccess: Boolean,

    @CreationTimestamp
    val createdAt: LocalDateTime? = null
)
