package hu.bme.aut.resource_server.ability

import hu.bme.aut.resource_server.utils.AbilityType
import jakarta.persistence.*

@Entity
@Table(name = "ABILITY")
data class AbilityEntity(
    @Id
    val code: String,

    val name: String,

    val description: String,

    @Column(name = "ability_type")
    @Enumerated(EnumType.STRING)
    val type: AbilityType = AbilityType.FLOATING,
)