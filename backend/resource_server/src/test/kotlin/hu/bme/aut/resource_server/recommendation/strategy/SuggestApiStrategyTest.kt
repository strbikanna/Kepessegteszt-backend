package hu.bme.aut.resource_server.recommendation.strategy

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.user.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class SuggestApiStrategyTest(
    @Autowired private var suggestApiStrategy: SuggestApiStrategy,
    @Autowired private var testService: TestUtilsService
) {
    @BeforeEach
    fun setUp() {
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
        runBlocking {
            val suggestedConfig = suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
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
    fun `Should not throw when params out of bound`() {
        testRecommendation.config = configItems.associate { it.paramName to
                if (it.increment < 0) it.hardestValue - 1 else it.easiestValue - 1
        }
        runBlocking {
            val suggestedConfig = suggestApiStrategy.generateRecommendationByResult(
                username = testUser.username,
                gameId = testGame.id!!,
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