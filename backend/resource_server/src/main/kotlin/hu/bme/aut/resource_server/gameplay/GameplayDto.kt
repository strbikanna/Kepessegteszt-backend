package hu.bme.aut.resource_server.gameplay

data class GameplayDto(

    val result: Map<String, Any?>,

    val username: String,

    val gameId: Int
)