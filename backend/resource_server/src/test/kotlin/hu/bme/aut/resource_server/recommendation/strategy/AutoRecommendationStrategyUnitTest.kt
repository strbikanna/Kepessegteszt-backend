package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import hu.bme.aut.resource_server.profile_calculation.service.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.user.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class AutoRecommendationStrategyUnitTest {
@Mock
private lateinit var mockDataService: ResultForCalculationDataService



private lateinit var autoRecommendationService : AutoRecommendationStrategy

    private lateinit var game: GameEntity
    private lateinit var user: UserEntity
    private lateinit var bestResult : ResultForCalculationEntity
    private lateinit var latestResult : ResultForCalculationEntity
    @BeforeEach
    fun init(){
        autoRecommendationService = AutoRecommendationStrategy(mockDataService)
        game = TestDataSource.createGameForTest()
        game.id = 1
        user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0]
        user.id = 1
        user.profileFloat.add(
            FloatProfileItem(ability = TestDataSource.affectedAbility , abilityValue = 0.5)
        )
        bestResult = TestDataSource.createNormalizedResultForUser(user, 0.7, game)
        latestResult = ResultForCalculationEntity(
            user = user,
            game = game,
            result = mutableMapOf(
                "level" to 2,
                "round" to 8,
                "maxRound" to 10,
                "healthPoints" to 8,
                "maxHealthPoints" to 9,
            ),
            normalizedResult = null,
            config = mutableMapOf(),
        )
    }


    @Test
    fun `Should Recommend Harder When Result Is Success`(){
        val latestRecommendation = TestDataSource.createRecommendationForUser(user, game).copy(timestamp = LocalDateTime.now())
        val result = TestDataSource.createGameplayResultForUser(user, latestRecommendation).copy(result = mapOf("passed" to true))
        `when`(mockDataService.getGameWithConfigItems(1)).thenReturn(game)
        runBlocking {
            val nextRecommendation =  autoRecommendationService.generateRecommendationByResult(
                user.username,
                game.id!!,
                latestRecommendation.id!!,
                result.config,
                true
            )
            assertEquals(1,
                game.configItems.filter{nextRecommendation.get(it.paramName) == it.initialValue + it.increment}.size
            )
        }
    }




    @Test
    fun `should change param even if that is on min value when success`(){
        val latestRecommendation = TestDataSource.createRecommendationForUser(user, game).copy(timestamp = LocalDateTime.now(),
            config = game.configItems.associate { it.paramName to it.easiestValue }.toMutableMap())
        `when`(mockDataService.getGameWithConfigItems(1)).thenReturn(game)
        runBlocking {
            val nextRecommendation =  autoRecommendationService.generateRecommendationByResult(
                user.username,
                game.id!!,
                latestRecommendation.id!!,
                latestRecommendation.config,
                true
            )
            assertEquals(2, nextRecommendation.size)
            assertEquals(
                1,
                game.configItems.filter{ nextRecommendation[it.paramName] == it.easiestValue + it.increment}.size
            )
        }
    }

    /**
     * When increment is 0, the value should not change
     */
    @Test
    fun `should work with 0 increment`(){
        game = game.also{ it.configItems = game.configItems.map { configItem -> configItem.copy(increment = 0) }.toMutableSet()}
        val latestRecommendation = TestDataSource.createRecommendationForUser(user, game).copy(timestamp = LocalDateTime.now(),
            config = game.configItems.associate { it.paramName to it.initialValue }.toMutableMap())
        val firstOrderParam = game.configItems.find { it.paramName == "speed" }!!
        val secondOrderParam = game.configItems.find { it.paramName == "timeLimit" }!!
        `when`(mockDataService.getGameWithConfigItems(1)).thenReturn(game)
        runBlocking {
            val nextRecommendation =  autoRecommendationService.generateRecommendationByResult(
                user.username,
                game.id!!,
                latestRecommendation.id!!,
                latestRecommendation.config,
                true
            )
            assertEquals(2, nextRecommendation.size)
            assertEquals(firstOrderParam.initialValue, nextRecommendation[firstOrderParam.paramName])
            assertEquals(secondOrderParam.initialValue, nextRecommendation[secondOrderParam.paramName])
        }
    }

    @Test
    fun `should work with Negyszog Blokkok`(){
        val negyszogBlokkok = game.also {
            it.configItems = mutableSetOf(
                ConfigItem(
                    id = 1,
                    paramName = "height",
                    maxAbilityEffect = 1.0,
                    initialValue = 8,
                    increment = 1,
                    easiestValue = 6,
                    hardestValue = 10,
                    description = ""
                ),
                ConfigItem(
                    id = 2,
                    paramName = "width",
                    maxAbilityEffect = 2.0,
                    initialValue = 8,
                    increment = 1,
                    easiestValue = 6,
                    hardestValue = 10,
                    description = ""
                ),
                ConfigItem(
                    id = 3,
                    paramName = "time_limit",
                    maxAbilityEffect = 1.0,
                    initialValue = 180000,
                    increment = 30000,
                    easiestValue = 300000,
                    hardestValue = 30000,
                    description = ""
                )
            )
        }
        val latestRecommendation = TestDataSource.createRecommendationForUser(user, negyszogBlokkok).copy(timestamp = LocalDateTime.now(),
            config = negyszogBlokkok.configItems.associate { it.paramName to it.initialValue }.toMutableMap())
        `when`(mockDataService.getGameWithConfigItems(1)).thenReturn(negyszogBlokkok)
        runBlocking {
            val nextRecommendation =  autoRecommendationService.generateRecommendationByResult(
                user.username,
                negyszogBlokkok.id!!,
                latestRecommendation.id!!,
                latestRecommendation.config,
                true
            )
            assertEquals(3, nextRecommendation.size)
            assertEquals(1, negyszogBlokkok.configItems.filter{ nextRecommendation[it.paramName] == it.initialValue + it.increment}.size)
        }
    }


}