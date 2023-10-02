package hu.bme.aut.resource_server.ability

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Ability(
    @Id
    val code: String,

    val name: String,

    val description: String
)
