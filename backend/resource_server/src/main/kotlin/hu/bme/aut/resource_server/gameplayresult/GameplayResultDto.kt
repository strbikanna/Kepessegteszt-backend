package hu.bme.aut.resource_server.gameplayresult

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for the result of a gameplay.
 * Games send their data in this format.
 */
data class GameplayResultDto(

    val result: Map<String, Any>,

    /**
     * The id of the recommended game that was played.
     */
    @JsonProperty("game_id")
    val gameplayId: Long,

    ) {
}