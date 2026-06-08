package hu.bme.aut.resource_server.recommendation.strategy


interface RecommendationStrategy {
    suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        recommendedGameId: Long,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
    ): Map<String, Any>
}