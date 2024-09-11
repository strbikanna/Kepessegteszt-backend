package hu.bme.aut.resource_server.recommended_game

data class RecommendationDto(
    val gameId: Int,
    val config: Map<String, Int>,
    val recommendedTo: String
)
