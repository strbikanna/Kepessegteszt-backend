package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameService
import hu.bme.aut.resource_server.result.ResultEntity
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RecommendationIntegrationTest(
    @Autowired private val recommendedGameService: RecommendedGameService,
    @Autowired private var autoRecommendationService: AutoRecommendationService,
    @Autowired private val testService: TestUtilsService
) {
    private val user = testService.createUnsavedTestUser()

    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        testService.saveUser(user)
    }

    @Test
    fun shouldFindRecommendationThatIsGeneratedLater() {
        val oldRGame = createRecommendedGame()
        testService.recommendedGameRepository.save(oldRGame)
        val newRGame = createRecommendedGame().copy(config = mapOf())
        testService.recommendedGameRepository.save(newRGame)
        val result = testService.resultRepository.save(createResult(oldRGame))
        val config = runBlocking {
            println("Starting")
            CoroutineScope(Dispatchers.Default).launch {
                println("Delay...")
                delay(500)
                println("Saving config")
                val createdConfig = autoRecommendationService.createNextRecommendationBasedOnResult(result.id!!)
                newRGame.config = createdConfig
                testService.recommendedGameRepository.save(newRGame)
                println("Config saved")
            }
            val found = CoroutineScope(Dispatchers.Default).async {
                recommendedGameService.getRecommendedGameConfig(newRGame.id!!)
            }.await()

            return@runBlocking found
        }
        assertTrue(config!! .isNotEmpty())
    }

    private fun createRecommendedGame(): RecommendedGameEntity {
        val configItem = ConfigItem(
            paramName = "Level",
            paramOrder = 1,
            easiestValue = 1,
            hardestValue = 10,
            increment = 1,
            description = "Level of the game",
            initialValue = 1
        )
        val game = testService.createAndSaveGame()
        game.configItems.add(configItem)
        testService.gameRepository.save(game)
        return RecommendedGameEntity(
            game = game,
            recommendedTo = user,
            config = mapOf("Level" to 1),
            completed = true
        )
    }

    private fun createResult(rGame: RecommendedGameEntity): ResultEntity {
        return ResultEntity(
            result = mapOf("passed" to true),
            passed = false,
            config = mutableMapOf("Level" to 1),
            user = user,
            recommendedGame = rGame
        )
    }
}