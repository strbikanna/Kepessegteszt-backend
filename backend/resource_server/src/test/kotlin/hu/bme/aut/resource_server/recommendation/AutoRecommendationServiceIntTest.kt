package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import hu.bme.aut.resource_server.result.ResultEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
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
            //empty because game has no config items
            assertTrue(recommendation.isEmpty())
        }

    }

    @Test
    fun `Should recommend harder if result is passed and game has configItems`(){
        val result = createResult() //passed is true
        val game = result.recommendedGame.game
        val currRecommendation = result.recommendedGame
        val configItems = TestDataSource.createGameForTest().configItems
        game.configItems.addAll(configItems)
        val config = mutableMapOf<String, Any>()
        configItems.forEach {
            config[it.paramName] = it.initialValue
        }
        currRecommendation.config = config
        testService.gameRepository.save(game)
        testService.recommendedGameRepository.save(currRecommendation)
        runBlocking {
            val recommendation = autoRecommendationService.createNextRecommendationBasedOnResult(result.id!!)
            assertTrue(recommendation.isNotEmpty())
            val changedParam = configItems.find { it.paramOrder == 1 }!!
            assertEquals(changedParam.initialValue + changedParam.increment, recommendation[changedParam.paramName]!!)
        }
    }

    private fun createResult(): ResultEntity{
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        val result = testService.createGamePlayResult(user)
        testService.resultRepository.save(result)
        return result
    }

}