package hu.bme.aut.resource_server.recommendation

interface RecommendationGenerator {
    suspend fun createNextRecommendationBasedOnResult(resultId: Long, generatorChain: List<RecommendationGenerator> = emptyList()): Map<String, Any>
}