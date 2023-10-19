package hu.bme.aut.resource_server.game


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
    @Autowired private var gameRepository: GameRepository
) {
    @BeforeEach
    fun emptyRepo() {
        gameRepository.deleteAll()
    }

    @Transactional
    @Test
    fun shouldSaveGame() {
        val config = mapOf<String, Any>("Level" to 0)
        val game = GameEntity(
            name = "TestGame", description = "test_game_description", icon="test", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", configDescription = config)
        gameRepository.save(game)
        assertNotNull(game.id)
    }

    @Transactional
    @Test
    fun shouldSaveMultipleGames() {
        val config1 = mapOf<String, Any>("Level" to 0)
        val config2 = mapOf<String, Any>("Level" to 3, "Ability" to "Gf")
        val game1 = GameEntity(
                name = "TestGame1", description = "test_game_description1", icon="test1", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game1",
                active = true, url = "test_game_url1", configDescription = config1)
        val game2 = GameEntity(
                name = "TestGame2", description = "test_game_description2", icon="test2", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game2",
                active = true, url = "test_game_url2", configDescription = config2)
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
        val config = mapOf<String, Any>("Level" to 0)
        val game = GameEntity(
                name = "TestGame", description = "test_game_description", icon="test", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", configDescription = config)
        gameRepository.save(game)
        assertNotNull(game.id)
        assertEquals(gameRepository.findAll().toList().size, 1)
        gameRepository.deleteById(game.id!!)
        assertEquals(gameRepository.findAll().toList().size, 0)
    }
}