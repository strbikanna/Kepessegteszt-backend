package hu.bme.aut.resource_server.recommendation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class StoredRecommendationServiceTest{

    @Test
    fun shouldGetRecommendationById(){
        val recommendation = StoredRecommendationService.getStoredRecommendationById(1)
        assertEquals(1, recommendation["id"])
    }

    @Test
    fun shouldHaveCorrectJsonFormat(){
        val recommendation = StoredRecommendationService.getStoredRecommendationById(1)
        assertTrue(recommendation.containsKey("id"))
        assertTrue(recommendation.containsKey("difficulty"))
        assertEquals(3, (recommendation["blackBlocks"] as List<Any>).size)
        assertEquals(1, ((recommendation["blackBlocks"] as List<Any>)[0] as Map<String, Any>)["block"])
        assertEquals(7, ((recommendation["blackBlocks"] as List<Any>)[0] as Map<String, Any>)["x"])
        assertEquals(5, ((recommendation["blackBlocks"] as List<Any>)[0] as Map<String, Any>)["y"])
    }
}