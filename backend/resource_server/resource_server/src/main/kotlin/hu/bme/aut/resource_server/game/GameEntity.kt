package hu.bme.aut.resource_server.game

import jakarta.persistence.*

@Entity
@Table(name = "GAMES")
data class GameEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val name: String,

    val description: String,

    val icon: String,

    val active: Boolean,

    val url: String,

    val jsonDescriptor: String
)