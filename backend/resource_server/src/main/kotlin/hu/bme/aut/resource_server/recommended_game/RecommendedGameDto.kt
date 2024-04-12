package hu.bme.aut.resource_server.recommended_game

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class RecommendedGameDto(
        val id: Long,

        val gameId: Int,

        val name: String,

        val description: String,

        val thumbnail: String,

        @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
        val recommendationDate: LocalDateTime,

        val recommender: String,
) {
}