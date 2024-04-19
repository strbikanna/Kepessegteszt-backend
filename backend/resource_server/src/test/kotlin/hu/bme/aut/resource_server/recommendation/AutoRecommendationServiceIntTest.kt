package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.gameplayresult.GameplayResultEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AutoRecommendationServiceIntTest(
    @Autowired val autoRecommendationService: AutoRecommendationService,
    @Autowired val testService: TestUtilsService
) {
    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }

    @Test
    fun shouldRunTransactionalSuspendFunction(){
        val result = createResult()
        runBlocking {
            val recommendation = autoRecommendationService.createNextRecommendationBasedOnResult(result.id!!)
            assertTrue(recommendation.isEmpty())
        }

    }

    private fun createResult(): GameplayResultEntity{
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        val result = testService.createGamePlayResult(user)
        testService.gameplayResultRepository.save(result)
        return result
    }

}