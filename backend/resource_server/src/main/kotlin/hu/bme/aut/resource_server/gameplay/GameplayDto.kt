package hu.bme.aut.resource_server.gameplay

data class GameplayDto(

    val gameResult: Map<String, Any?>,

    val username: String,

    val gameId: Int
) {
}