package hu.bme.aut.resource_server.gameplayresult

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for the result of a gameplay.
 * Games send their data in this format.
 */
data class GameplayResultDto(

    @JsonProperty("gameResults")
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
    @JsonProperty("gameParams")
    val config: Map<String, Any>
) {
}