package hu.bme.aut.resource_server.game

import jakarta.persistence.*

@Entity
@Table(name = "games")
data class GameEntity(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int? = null,

    val name: String,

    val description: String,

    val icon: String,

    val active: Boolean,

    val url: String,

    val jsonDescriptor: String
)