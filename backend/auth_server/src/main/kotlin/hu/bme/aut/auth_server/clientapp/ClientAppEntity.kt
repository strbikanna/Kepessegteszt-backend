package hu.bme.aut.auth_server.clientapp

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "CLIENTS")
data class ClientAppEntity(
    @Id
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
