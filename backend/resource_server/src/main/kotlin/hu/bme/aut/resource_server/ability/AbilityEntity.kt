package hu.bme.aut.resource_server.ability

import hu.bme.aut.resource_server.utils.AbilityType
import jakarta.persistence.*

/**
 * Entity class representing cognitive abilities.
 */
@Entity
@Table(name = "ability")
data class AbilityEntity(
    /**
     * CHC or other code name of ability. Unique.
     */
    @Id
    val code: String,

    val name: String,

    val description: String,

    @Column(name = "ability_type")
    @Enumerated(EnumType.STRING)
    val type: AbilityType = AbilityType.FLOATING,
)
