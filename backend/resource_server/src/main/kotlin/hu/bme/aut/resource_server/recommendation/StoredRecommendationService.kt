package hu.bme.aut.resource_server.recommendation

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource

object StoredRecommendationService {

    @Value("\${recommendation.location}")
    private lateinit var storedRecommendationLocation: String

    fun getStoredRecommendationById(id: Int): Map<String, Any>{
        val resource = ClassPathResource("$storedRecommendationLocation/$id.json")
        val fileContent = resource.inputStream.bufferedReader().use { it.readText() }
        val objectMapper = jacksonObjectMapper()
        val map: Map<String, Any> = objectMapper.readValue(fileContent)
        return map
    }
}