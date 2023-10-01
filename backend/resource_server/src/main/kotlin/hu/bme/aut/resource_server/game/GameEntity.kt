package hu.bme.aut.resource_server.game

import jakarta.persistence.*

@Entity
@Table(name = "GAMES")
data class GameEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val game_name: String,

    val game_description: String

    val icon: String,

    val game_active: Boolean,

    val url: String,

    val json_descriptor: String
)