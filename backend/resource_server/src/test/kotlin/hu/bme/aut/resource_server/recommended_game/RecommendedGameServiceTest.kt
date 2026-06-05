package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.StoredConfigGameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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
        val savedGames = testService.gameRepository.saveAll(listOf(game1, game2, game3, game4))
        val acceptedIs = savedGames.map { it.id!! }
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
        val nextChoice = recommendedGameService.getNextChoiceForUser(user.username, acceptedIs)
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
        assertEquals(1, config?.get("Level"))
    }

    @Test
    fun shouldGetConfigOfDefaultGame() {
        val rGame = createRecommendedGame()
        testService.recommendedGameRepository.save(rGame)
        val config = runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                val config = mapOf("Level" to 1)
                val savedRGame = testService.recommendedGameRepository.findById(rGame.id!!).get()
                savedRGame.config = config
                testService.recommendedGameRepository.save(savedRGame)
            }
            val found = CoroutineScope(Dispatchers.Default).async {
                recommendedGameService.getRecommendedGameConfig(rGame.id!!)
            }.await()

            return@runBlocking found
        }
        assertEquals(1, config?.get("Level"))
    }

    @Test
    fun shouldGetStoredConfigOfGame() {
        val storedConfigGame = StoredConfigGameEntity(
            version = 1,
            name = "Test game",
            description = "Test game description",
            thumbnailPath = "test.jpg",
            active = true,
            configItems = mutableSetOf(
                ConfigItem(
                    paramName = "level",
                    initialValue = 1,
                    hardestValue = 88,
                    increment = 1,
                    easiestValue = 1,
                    description = "Level of the game",
                    maxAbilityEffect = 2.0
                )
            )
        )
        val savedGame = testService.gameRepository.save(storedConfigGame)
        val rGame = RecommendedGameEntity(
            game = savedGame,
            recommendedTo = user,
            config = mapOf(),
            completed = false
        )
        testService.recommendedGameRepository.save(rGame)
        val config = runBlocking {
            CoroutineScope(Dispatchers.Default).launch {
                val config = mapOf("level" to 1)
                val savedRGame = testService.recommendedGameRepository.findById(rGame.id!!).get()
                savedRGame.config = config
                testService.recommendedGameRepository.save(savedRGame)
            }
            val found = CoroutineScope(Dispatchers.Default).async {
                recommendedGameService.getRecommendedGameConfig(rGame.id!!)
            }.await()

            return@runBlocking found
        }
        assertEquals(1, config!!["id"])
        assertEquals("EASY", config["difficulty"])

    }

    @Test
    fun `getRecommendationsToUserAndGame should return only active games when gameId is null`() {
        // create an active and an inactive game
        val activeGame = createGame()
        val inactiveGame = createGame()
        inactiveGame.active = false
        val savedActive: GameEntity = testService.gameRepository.save(activeGame)
        val savedInactive: GameEntity = testService.gameRepository.save(inactiveGame)

        // create recommendations for both games
        val rActive = RecommendedGameEntity(
            game = savedActive,
            recommendedTo = user,
            config = mapOf(),
            completed = false
        )
        val rInactive = RecommendedGameEntity(
            game = savedInactive,
            recommendedTo = user,
            config = mapOf(),
            completed = false
        )
        testService.recommendedGameRepository.save(rActive)
        testService.recommendedGameRepository.save(rInactive)

        val results = recommendedGameService.getRecommendationsToUserAndGame(user.username, null, null)
        // should only contain the recommendation for the active game
        assertEquals(1, results.size)
        assertEquals(savedActive.id, results[0].gameId)
    }

    @Test
    fun `getRecommendationsToUserAndGame with gameId should return only recommendations for that game`() {
        val game1 = testService.createAndSaveGame()
        val game2 = testService.createAndSaveGame()

        // two recommendations for game1 and one for game2
        val r1 = RecommendedGameEntity(game = game1, recommendedTo = user, config = mapOf(), completed = false)
        val r2 = RecommendedGameEntity(game = game1, recommendedTo = user, config = mapOf(), completed = true)
        val r3 = RecommendedGameEntity(game = game2, recommendedTo = user, config = mapOf(), completed = false)
        testService.recommendedGameRepository.saveAll(listOf(r1, r2, r3))

        val results = recommendedGameService.getRecommendationsToUserAndGame(user.username, game1.id, null)
        // should return only the two recommendations that belong to game1
        assertEquals(2, results.size)
        assertTrue(results.all { it.gameId == game1.id })
    }

    @Test
    fun `getAllRecommendedToUser should return only non completed active games and respect acceptedGameIds`() {
        // create active and inactive games
        val activeGame = createGame()
        val inactiveGame = createGame().apply { active = false }
        val savedActive = testService.gameRepository.save(activeGame)
        val savedInactive = testService.gameRepository.save(inactiveGame)

        // create recommendations: one active & incomplete, one inactive & incomplete, one active & completed
        val rActiveIncomplete = RecommendedGameEntity(game = savedActive, recommendedTo = user, config = mapOf(), completed = false)
        val rInactiveIncomplete = RecommendedGameEntity(game = savedInactive, recommendedTo = user, config = mapOf(), completed = false)
        val rActiveCompleted = RecommendedGameEntity(game = savedActive, recommendedTo = user, config = mapOf(), completed = true)
        testService.recommendedGameRepository.saveAll(listOf(rActiveIncomplete, rInactiveIncomplete, rActiveCompleted))

        // when no acceptedGameIds provided, only active and not completed should be returned
        val results = recommendedGameService.getAllRecommendedToUser(user.username, null)
        assertEquals(1, results.size)
        assertEquals(savedActive.id, results[0].gameId)

        // when acceptedGameIds contains only the inactive game's id, result should be empty (inactive filtered out)
        val resultsAcceptedOnlyInactive = recommendedGameService.getAllRecommendedToUser(user.username, listOf(savedInactive.id!!))
        assertEquals(0, resultsAcceptedOnlyInactive.size)

        // when acceptedGameIds contains the active game's id, it should be returned
        val resultsAcceptedActive = recommendedGameService.getAllRecommendedToUser(user.username, listOf(savedActive.id!!))
        assertEquals(1, resultsAcceptedActive.size)
        assertEquals(savedActive.id, resultsAcceptedActive[0].gameId)
    }

    @Test
    fun `addRecommendation should save recommendation and set recommender and recommendedTo correctly`() {
        // create and save a recommender user
        val recommenderUser = testService.createUnsavedTestUser().copy(username = "recommender_user")
        testService.saveUser(recommenderUser)

        // ensure target user and game exist
        val targetUser = user
        val game = testService.createAndSaveGame()

        val dto = RecommendationDto(game.id!!, mapOf("level" to 1), targetUser.username)
        val savedRecommendation = recommendedGameService.addRecommendation(dto, recommenderUser.username)

        // validate the saved recommendation
        assertEquals(targetUser.username, savedRecommendation.recommendedTo.username)
        assertNotNull(savedRecommendation.recommender)
        assertEquals(recommenderUser.username, savedRecommendation.recommender!!.username)
        assertEquals(game.id, savedRecommendation.game.id)
        // config is at least not empty (special settings may be applied)
        assertTrue(savedRecommendation.config.isNotEmpty())
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
            affectedAbilities = mutableSetOf()
        )
    }

}