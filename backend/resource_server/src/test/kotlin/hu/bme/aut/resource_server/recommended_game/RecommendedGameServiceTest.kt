package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.GameEntity
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class RecommendedGameServiceTest(
    @Autowired private val recommendedGameService: RecommendedGameService,
    @Autowired private val testService: TestUtilsService
) {

    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        testService.saveUser(user)
    }

    private val user = testService.createUnsavedTestUser()

    @Test
    fun getNextChoiceForUser() {
        val game1 = createGame()
        val game2 = createGame()
        val game3 = createGame()
        val game4 = createGame()
        testService.gameRepository.saveAll(listOf(game1, game2, game3, game4))
        val rGameList = listOf(
            createRecommendedGame().copy(
                game = game1,
                timestamp = LocalDateTime.of(2024, 10, 1, 0, 0),
                completed = false
            ),
            createRecommendedGame().copy(
                game = game2,
                timestamp = LocalDateTime.of(2024, 10, 2, 0, 0),
                completed = false
            ),
            createRecommendedGame().copy(
                game = game3,
                timestamp = LocalDateTime.of(2024, 9, 2, 0, 0),
                completed = false
            ),
            createRecommendedGame().copy(
                game = game4,
                timestamp = LocalDateTime.of(2024, 9, 22, 0, 0),
                completed = false
            ),
            createRecommendedGame().copy(
                game = game1,
                timestamp = LocalDateTime.of(2024, 8, 1, 0, 0),
            ),
            createRecommendedGame().copy(
                game = game2,
                timestamp = LocalDateTime.of(2024, 8, 5, 0, 0),
            ),
            createRecommendedGame().copy(
                game = game3,
                timestamp = LocalDateTime.of(2024, 9, 1, 0, 0),
                completed = false
            ),
            createRecommendedGame().copy(
                game = game4,
                timestamp = LocalDateTime.of(2024, 9, 5, 0, 0),
            ),
        )
        testService.recommendedGameRepository.saveAll(rGameList)
        val nextChoice = recommendedGameService.getNextChoiceForUser(user.username)
        assertEquals(2, nextChoice.size)
        assertTrue(nextChoice.any { it.gameId == game1.id })
        assertTrue(nextChoice.any { it.gameId == game3.id })
    }

    @Test
    fun shouldReturnConfigOfGameEvenIfDelayed() {
        val rGame = createRecommendedGame()
        testService.recommendedGameRepository.save(rGame)
        val config = runBlocking {
            println("Starting")
            CoroutineScope(Dispatchers.Default).launch {
                println("Delay...")
                delay(500)
                println("Saving config")
                val delayedConfig = mapOf("Level" to 1)
                val savedRGame = testService.recommendedGameRepository.findById(rGame.id!!).get()
                savedRGame.config = delayedConfig
                testService.recommendedGameRepository.save(savedRGame)
                println("Config saved")
            }
            val found = CoroutineScope(Dispatchers.Default).async {
                recommendedGameService.getRecommendedGameConfig(rGame.id!!)
            }.await()

            return@runBlocking found
        }
        assertEquals(mapOf("Level" to 1), config)
    }

    private fun createRecommendedGame(): RecommendedGameEntity {
        val game = testService.createAndSaveGame()
        return RecommendedGameEntity(
            game = game,
            recommendedTo = user,
            config = mapOf(),
            completed = true
        )
    }

    private fun createGame(): GameEntity {
        return GameEntity(
            version = 1,
            name = "Test game",
            thumbnailPath = "test/files/assets",
            description = "Test game description",
            active = true,
            configDescription = mutableMapOf("Level" to 0),
            affectedAbilities = mutableSetOf()
        )
    }

}