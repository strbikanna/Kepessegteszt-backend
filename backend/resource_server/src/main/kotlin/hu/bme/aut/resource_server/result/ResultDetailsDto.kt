package hu.bme.aut.resource_server.result

import java.time.LocalDateTime

data class ResultDetailsDto(
    val id: Long,
    val timestamp: LocalDateTime,
    val result: Map<String, Any>,
    val config: Map<String, Any>,
    val gameId: Int,
    val username: String
) {
}