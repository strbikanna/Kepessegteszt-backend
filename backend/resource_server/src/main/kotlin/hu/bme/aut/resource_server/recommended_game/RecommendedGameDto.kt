package hu.bme.aut.resource_server.recommended_game

import java.time.LocalDateTime

data class RecommendedGameDto(
        val id: Long,
        val name: String,
        val description: String,
        val thumbnail: String,
        val recommendationDate: LocalDateTime,
        val recommender: String,
) {
}