package hu.bme.aut.resource_server.gameplayresult

import com.fasterxml.jackson.annotation.JsonProperty


data class GameplayResultDto(

    val gameResult: Map<String, Any?>,

    val username: String,

    /**
     * The id of the recommended game that was played.
     */
    @JsonProperty("game_id")
    val gameplayId: Long,

    /**
     * The actual configuration of the game that was played.
     * Not necessarily the same as the recommended game's configuration.
     */
    val config: Map<String, Any>
) {
}