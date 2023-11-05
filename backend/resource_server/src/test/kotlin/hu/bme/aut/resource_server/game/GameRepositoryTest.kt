package hu.bme.aut.resource_server.game


import hu.bme.aut.resource_server.TestUtilsService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class GameRepositoryTest(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var testUtilsService: TestUtilsService
) {
    @BeforeEach
    fun emptyRepo() {
        gameRepository.deleteAll()
    }

    @Transactional
    @Test
    fun shouldSaveGame() {
        val config = mutableMapOf<String, Any>("Level" to 0)
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", configDescription = config, affectedAbilites = abilities)
        gameRepository.save(game)
        assertNotNull(game.id)
    }

    @Transactional
    @Test
    fun testFindByName() {
        val config = mutableMapOf<String, Any>("Level" to 0)
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", configDescription = config, affectedAbilites = abilities)
        println(game.affectedAbilites.toString())
        gameRepository.save(game)
        assertNotNull(game.id)
        assertTrue(gameRepository.existsByName("TestGame"))
        assertEquals(Optional.of(game), gameRepository.findGameByName("TestGame"))
    }

    @Transactional
    @Test
    fun shouldSaveMultipleGames() {
        val config1 = mutableMapOf<String, Any>("Level" to 0)
        val abilities1 = mutableSetOf(testUtilsService.abilityColorsense)
        val config2 = mutableMapOf<String, Any>("Level" to 3)
        val abilities2 = mutableSetOf(testUtilsService.abilityGf, testUtilsService.abilityGq)

        val game1 = GameEntity(
            version = 1, name = "TestGame1", description = "test_game_description1", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game1",
                active = true, url = "test_game_url1", configDescription = config1, affectedAbilites = abilities1)
        val game2 = GameEntity(
            version = 1, name = "TestGame2", description = "test_game_description2", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game2",
                active = true, url = "test_game_url2", configDescription = config2, affectedAbilites = abilities2)
        val savedGame1 = gameRepository.save(game1)
        val savedGame2 = gameRepository.save(game2)
        assertNotEquals(game1.id, game2.id)
        assertEquals(savedGame1.url, "test_game_url1")
        assertEquals(savedGame2.configDescription, config2)
        assertEquals(savedGame2.id, game2.id)
        assertEquals(gameRepository.findAll().toList().size, 2)
    }

    @Transactional
    @Test
    fun shouldSaveThenDeleteGame() {
        val config = mutableMapOf<String, Any>("Level" to 0)
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", configDescription = config, affectedAbilites = abilities)
        gameRepository.save(game)
        assertNotNull(game.id)
        assertEquals(gameRepository.findAll().toList().size, 1)
        gameRepository.deleteById(game.id!!)
        assertEquals(gameRepository.findAll().toList().size, 0)
    }
}