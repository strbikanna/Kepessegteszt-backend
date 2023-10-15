package hu.bme.aut.resource_server.gameplay

import org.json.JSONObject

data class GamePlayDto(

    val result: Map<String, Any?>,

    val username: String,

    val gameId: Int
) {
}