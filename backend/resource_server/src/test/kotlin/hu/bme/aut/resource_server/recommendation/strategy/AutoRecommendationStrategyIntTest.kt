package hu.bme.aut.resource_server.recommendation.strategy

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
class AutoRecommendationStrategyIntTest(
    @Autowired val autoRecommendationStrategy: AutoRecommendationStrategy,
    @Autowired val testService: TestUtilsService
) {
    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }

    private var username : String = "test_user"

    @Test
    fun shouldRunTransactionalSuspendFunction(){
        val result = createResult()
        runBlocking {
            val recommendation = autoRecommendationStrategy.generateRecommendationByResult(
                username,
                result.recommendedGame.game.id!!,
                result.config,
                true
            )
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
            val recommendation = autoRecommendationStrategy.generateRecommendationByResult(
                username,
                game.id!!,
                config,
                true
            )
            assertTrue(recommendation.isNotEmpty())
            assertEquals(1, game.configItems.filter{recommendation[it.paramName] == it.initialValue + it.increment}.size)
        }
    }

    private fun createResult(): ResultEntity{
        val user = testService.createUnsavedTestUser()
        username = user.username
        testService.saveUser(user)
        val result = testService.createGamePlayResult(user)
        testService.resultRepository.save(result)
        return result
    }

}