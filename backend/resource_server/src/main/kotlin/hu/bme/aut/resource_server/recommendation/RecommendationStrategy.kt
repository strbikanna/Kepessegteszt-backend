package hu.bme.aut.resource_server.recommendation


interface RecommendationStrategy {
    suspend fun generateRecommendationByResult(
        username: String,
        gameId: Int,
        previousConfig: Map<String, Any>,
        isResultSuccess: Boolean,
    ): Map<String, Any>
}