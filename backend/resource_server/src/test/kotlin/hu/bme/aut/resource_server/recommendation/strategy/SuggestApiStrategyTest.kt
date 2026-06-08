package hu.bme.aut.resource_server.recommendation.strategy

import com.github.tomakehurst.wiremock.client.WireMock.*
import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.result.ResultDto
import hu.bme.aut.resource_server.result.ResultService
import hu.bme.aut.resource_server.user.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import kotlin.math.abs


@SpringBootTest
@ActiveProfiles("test")
@EnableWireMock
@ConfigureWireMock(
        baseUrlProperties = ["app.suggest-api"],
)
class SuggestApiStrategyTest(
    @Autowired private var suggestApiStrategy: SuggestApiStrategy,
    @Autowired private var testService: TestUtilsService,
    @Autowired private var resultService: ResultService,
) {
    @Value("\${app.suggest-api}")
    private val wireMockUrl: String? = null

    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        testService.abilityRepository.saveAll(
            listOf(
                ability1,
                ability4,
                ability6,
                ability7,
                ability8,
                ability9,
                ability10
            )
        )
        testUser = testService.createUnsavedTestUser()
        testService.userRepository.save(testUser)
        testService.gameRepository.save(testGame)
        testRecommendation = RecommendedGameEntity(
            game = testGame,
            recommendedTo = testUser,
            config = configItems.associate { it.paramName to it.initialValue }
        )
        testService.recommendedGameRepository.save(testRecommendation)
    }

    @AfterEach
    fun tearDown() {
        testService.emptyRepositories()
    }

    @Test
    fun `Should call suggest api with correct params`() {
        registerWireMock(testGame.modelId!!,"")
        runBlocking {
            val suggestedConfig = suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
                recommendedGameId = testRecommendation.id!!,
                previousConfig = testRecommendation.config,
                isResultSuccess = true
            )
            assertEquals(4, suggestedConfig.size)
            val difference = suggestedConfig["difference"] as Int
            val answerTimelimit = suggestedConfig["answer_timelimit"] as Int
            val visibleTime = suggestedConfig["visible_time"] as Int
            val questionNumber = suggestedConfig["question_number"] as Int
            val isDiffHarder = difference in 300..2_000 //should be lower than initial value
            val isTimeLimitHarder = (answerTimelimit in 2_000..3_000) //should be lower than initial value
            val isVisibleTimeHarder = (visibleTime in 201..3_000) //should be lower than initial value
            val isQuestionNumHarder = (questionNumber in 10..15) //should be higher than initial value
            //at least 1 harder
            assertTrue(
                listOf(
                    isDiffHarder,
                    isTimeLimitHarder,
                    isVisibleTimeHarder,
                    isQuestionNumHarder
                ).count { it } >= 1
            )

        }
    }


    @Test
    fun `Should not throw when params out of bound`() {
        registerWireMock( testGame.modelId!!,"""
                            {
                                "abilities": [0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
                                "prev_params": {
                                    "difference": 299,
                                    "answer_timelimit": 1999,
                                    "visible_time": 200,
                                    "question_number": 4
                                 },
                                "result": true
                            }
                        """)
        testRecommendation.config = configItems.associate {
            it.paramName to
                    if (it.increment < 0) it.hardestValue - 1 else it.easiestValue - 1
        }
        runBlocking {
            val suggestedConfig = suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
                recommendedGameId = testRecommendation.id!!,
                previousConfig = testRecommendation.config,
                isResultSuccess = true
            )
            assertEquals(4, suggestedConfig.size)
            val difference = suggestedConfig["difference"] as Int
            val answerTimelimit = suggestedConfig["answer_timelimit"] as Int
            val visibleTime = suggestedConfig["visible_time"] as Int
            val questionNumber = suggestedConfig["question_number"] as Int
            assertTrue(difference in 300..10_000)
            assertTrue(answerTimelimit in 2_000..6_000)
            assertTrue(visibleTime in 201..5_000)
            assertTrue(questionNumber in 5..15)
        }
    }

    @Test
    fun `should create user profile updates`() {
        registerWireMock(testGame.modelId!!,"")
        runBlocking {
            suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
                recommendedGameId = testRecommendation.id!!,
                previousConfig = testRecommendation.config,
                isResultSuccess = true
            )
            val updatedRecommendation =
                testService.recommendedGameRepository.findByIdWithProfileUpdateItems(testRecommendation.id!!).get()
            updatedRecommendation.profileUpdateItems.forEach {
                assertNotNull(it.updatedValue)
            }
            val successAbilityUpdates = updatedRecommendation.profileUpdateItems.filter { it.validOnSuccess }
            val failureAbilityUpdates = updatedRecommendation.profileUpdateItems.filter { !it.validOnSuccess }
            assertEquals(successAbilityUpdates.size, failureAbilityUpdates.size)
            successAbilityUpdates.forEach {
                val correspondingFailureUpdate = failureAbilityUpdates.find { fa -> it.ability == fa.ability }
                assertNotNull(correspondingFailureUpdate)
                assertTrue(it.updatedValue >= correspondingFailureUpdate!!.updatedValue)
            }

        }
    }

    @Test
    fun `user profile should be updated when saving result based on suggested update`() {
        registerWireMock(testGame.modelId!!,"")
        runBlocking {
            suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
                recommendedGameId = testRecommendation.id!!,
                previousConfig = testRecommendation.config,
                isResultSuccess = true
            )
            val resultPassed = ResultDto(
                result = mapOf(Pair("passed", true)),
                gameplayId = testRecommendation.id!!,
            )
            resultService.updateProfileByResult(resultPassed)
            val updatedUser = testService.userRepository.findByIdWithProfile(testUser.id!!).get()
            val recommendedUpdates =
                testService.recommendedGameRepository.findByIdWithProfileUpdateItems(testRecommendation.id!!)
                    .get().profileUpdateItems
            recommendedUpdates
                .filter { it.validOnSuccess }
                .forEach { updateItem ->
                    val userAbility = updatedUser.profileFloat.find { it.ability.code == updateItem.ability.code }
                    assertNotNull(userAbility)
                    assertTrue(0.0001 > abs(updateItem.updatedValue - userAbility!!.abilityValue))

                }
        }
    }

    private fun registerWireMock(gameModelId: String, requestBodyJson: String) {
        stubFor(
            post(
                urlEqualTo("/games/$gameModelId/suggest"),
            )
                .withRequestBody(
                    equalToJson( requestBodyJson.ifBlank {
                        """
                            {
                                "abilities": [0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
                                "prev_params": {
                                    "difference": 2000,
                                    "answer_timelimit": 3000,
                                    "visible_time": 3000,
                                    "question_number": 10
                                 },
                                "result": true
                            }
                        """.trimIndent()
                    })
                )
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody(
                            """
            {
              "abilities_if_failure": [0.0, 0.98, 0.97, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
              "abilities_if_success": [0.0, 1.11, 1.17, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],
              "success_rate": 0.75,
              "suggested_params": {
                "difference": 1500,
                "answer_timelimit": 2500,
                "visible_time": 2500,
                "question_number": 12
               }
            }
        """
                        )
                )
        );
    }

    private lateinit var testUser: UserEntity

    private val configItems = setOf(
        ConfigItem(
            paramName = "difference",
            easiestValue = 10_000,
            hardestValue = 300,
            initialValue = 2_000,
            maxAbilityEffect = 1.0,
            increment = -100,
            description = ""
        ),
        ConfigItem(
            paramName = "answer_timelimit",
            easiestValue = 6_000,
            hardestValue = 2_000,
            initialValue = 3_000,
            maxAbilityEffect = 1.0,
            increment = -200,
            description = ""
        ),
        ConfigItem(
            paramName = "visible_time",
            easiestValue = 5_000,
            hardestValue = 201,
            initialValue = 3_000,
            maxAbilityEffect = 1.0,
            increment = -100,
            description = ""
        ),
        ConfigItem(
            paramName = "question_number",
            easiestValue = 5,
            hardestValue = 15,
            initialValue = 10,
            maxAbilityEffect = 1.0,
            increment = 1,
            description = ""
        ),
    )

    private var testGame: GameEntity = GameEntity(
        modelId = "rocket",
        version = 1,
        name = "Rocket game",
        thumbnailPath = "test/files/assets",
        description = "Test game description",
        active = true,
        affectedAbilities = mutableSetOf(testService.abilityGf),
        configItems = configItems.toMutableSet()
    )

    private val ability1 = AbilityEntity(
        code = "Gc",
        modelIndex = 1,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )
    private val ability4 = AbilityEntity(
        code = "Grw",
        modelIndex = 4,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )

    private val ability6 = AbilityEntity(
        code = "Glr",
        modelIndex = 6,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )
    private val ability7 = AbilityEntity(
        code = "Gv",
        modelIndex = 7,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )
    private val ability8 = AbilityEntity(
        code = "Ga",
        modelIndex = 8,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )
    private val ability9 = AbilityEntity(
        code = "Gs",
        modelIndex = 9,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )
    private val ability10 = AbilityEntity(
        code = "Gt",
        modelIndex = 10,
        name = "Fluid intelligence",
        description = "Ability to discover the underlying characteristic that governs a problem or a set of materials."
    )

    private lateinit var testRecommendation: RecommendedGameEntity


}