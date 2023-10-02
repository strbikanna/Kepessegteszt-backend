package hu.bme.aut.resource_server.game

data class GameDao(
    val id: Int,

    val game_name: String,

    val game_description: String,

    val icon: String,

    val game_actuve: Boolean,

    val url: String,

    val json_descriptor: String
)