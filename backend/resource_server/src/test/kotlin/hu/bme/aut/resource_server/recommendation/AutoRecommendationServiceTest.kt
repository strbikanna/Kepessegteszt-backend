package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import hu.bme.aut.resource_server.profile_calculation.calculator.AbilityRateCalculatorService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AutoRecommendationServiceTest {
@Mock
private lateinit var mockDataService: ResultForCalculationDataService

@Mock
private lateinit var mockCalculatorService: AbilityRateCalculatorService

@Mock
private lateinit var mockModelManager: ModelManager


private lateinit var autoRecommendationService : AutoRecommendationService

    private lateinit var game: GameEntity
    private lateinit var user: UserEntity
    private lateinit var bestResult : ResultForCalculationEntity
    private lateinit var latestResult : ResultForCalculationEntity
    @BeforeEach
    fun init(){
        autoRecommendationService = AutoRecommendationService(mockDataService, mockCalculatorService, mockModelManager)
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
    fun shouldRecommendBasedOnResult_WhenModelNotExists(){
        Mockito.`when`(mockModelManager.existsModel(game.id!!)).thenReturn(false)
        Mockito.`when`(mockDataService.getBestResultOfUser(game, user)).thenReturn(bestResult)
        //levelPoints based on normalized result = 0.7 - 0.5 * 0.7 = 0.35
        //recommendedLevel = 0.35 * 10 * 2 = 7
        val recommendation = autoRecommendationService.generateRecommendationForUser(user, game)
        assertEquals(7, recommendation.config["level"])
        assertEquals(game, recommendation.game)
        assertEquals(user, recommendation.recommendedTo)
    }
    @Test
    fun shouldRecommendBasedOnModel_WhenModelExists(){
        Mockito.`when`(mockModelManager.existsModel(game.id!!)).thenReturn(true)
        Mockito.`when`(mockModelManager.getEstimationForResult(game.id!!, listOf(0.5))).thenReturn(0.3)
        //levelPoints based on normalized result = 0.3 - 0.7 * 0.5 = -0.05
        //recommendedLevel = 1
        val recommendation = autoRecommendationService.generateRecommendationForUser(user, game)
        assertEquals(1, recommendation.config["level"])
        assertEquals(game, recommendation.game)
        assertEquals(user, recommendation.recommendedTo)
    }

    @Test
    fun shouldRecommendBasedOnLatestResult_WhenBestResultAndModelNotExists(){
        Mockito.`when`(mockModelManager.existsModel(game.id!!)).thenReturn(false)
        Mockito.`when`(mockDataService.getBestResultOfUser(game, user)).thenReturn(null)
        Mockito.`when`(mockDataService.getLatestResultOfUser(game, user)).thenReturn(latestResult)
        //max score of level = 10+9 = 19
        //score = 8+8 = 16
        //successRatio = 16/19 = 0.84
        //successRation is exceeding 0.7 motivation value, so recommendedLevel = 3
        val recommendation = autoRecommendationService.generateRecommendationForUser(user, game)
        assertEquals(3, recommendation.config["level"])
        assertEquals(game, recommendation.game)
        assertEquals(user, recommendation.recommendedTo)
    }

    @Test
    fun shouldRecommendLevel1_WhenNothingExists(){
        Mockito.`when`(mockModelManager.existsModel(game.id!!)).thenReturn(false)
        Mockito.`when`(mockDataService.getBestResultOfUser(game, user)).thenReturn(null)
        Mockito.`when`(mockDataService.getLatestResultOfUser(game, user)).thenReturn(null)
        val recommendation = autoRecommendationService.generateRecommendationForUser(user, game)
        assertEquals(1, recommendation.config["level"])
        assertEquals(game, recommendation.game)
        assertEquals(user, recommendation.recommendedTo)
    }


}