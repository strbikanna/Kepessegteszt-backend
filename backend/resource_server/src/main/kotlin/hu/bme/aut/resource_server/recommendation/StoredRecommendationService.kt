package hu.bme.aut.resource_server.recommendation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import hu.bme.aut.resource_server.recommendation.visitor.GameConfigVisitor
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource

object StoredRecommendationService: GameConfigVisitor {

    @Value("\${recommendation.location}")
    private var storedRecommendationLocation: String = "block_descriptions"

    override fun visitConfig(config: Map<String, Any>): Map<String, Any> {
        val storedRecommendationId = config.values.first() as Int
        return try{
            getStoredRecommendationById(storedRecommendationId)
        }catch (e: Exception){
            throw IllegalArgumentException("Stored recommendation not found with id: $storedRecommendationId")
        }
    }

    private fun getStoredRecommendationById(id: Int): Map<String, Any>{
        val resource = ClassPathResource("$storedRecommendationLocation/$id.json")
        val fileContent = resource.inputStream.bufferedReader().use { it.readText() }
        val objectMapper = jacksonObjectMapper()
        val map: Map<String, Any> = objectMapper.readValue(fileContent)
        return map
    }
}