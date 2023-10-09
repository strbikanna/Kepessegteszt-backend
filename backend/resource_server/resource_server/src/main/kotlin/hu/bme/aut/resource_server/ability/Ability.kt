package hu.bme.aut.resource_server.ability

import hu.bme.aut.resource_server.utils.AbilityType
import jakarta.persistence.*

@Entity
data class Ability(
    @Id
    val code: String,

    val name: String,

    val description: String,

    @Column(name = "ability_type")
    @Enumerated(EnumType.STRING)
    val type: AbilityType = AbilityType.FLOATING,
)
