package hu.bme.aut.resource_server.user_group.organization

import jakarta.persistence.Embeddable

@Embeddable
data class Address(
        val houseNumber: String,
        val street: String,
        val city: String,
        val zip: String
)
