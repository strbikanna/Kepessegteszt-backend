package hu.bme.aut.resource_server.game.game_config

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "config_description_item")
data class ConfigItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    val paramName: String,

    val easiestValue: Int,

    val hardestValue: Int,

    val initialValue: Int,

    val increment: Int,

    val description: String
)
