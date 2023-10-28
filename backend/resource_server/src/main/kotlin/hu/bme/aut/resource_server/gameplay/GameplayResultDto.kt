package hu.bme.aut.resource_server.gameplay

import com.fasterxml.jackson.annotation.JsonProperty


data class GameplayResultDto(

    val gameResult: Map<String, Any?>,

    val username: String,

    @JsonProperty("game_id")
    val gameId: Int
) {
}