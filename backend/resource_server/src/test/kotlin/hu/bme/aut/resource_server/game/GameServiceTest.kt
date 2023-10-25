package hu.bme.aut.resource_server.game

import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Paths
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class GameServiceTest(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var gameService: GameService
) {
    @BeforeEach
    fun emptyRepo() {
        gameRepository.deleteAll()
    }

    @Transactional
    @Test
    fun shouldSaveGameThenReturnDto() {
        val config = mapOf<String, Any>("Level" to 0)
        val game = GameEntity(
                name = "TestGame", description = "test_game_description", icon="test", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
                active = true, url = "test_game_url", configDescription = config)
        gameRepository.save(game)
        assertNotNull(game.id)
        assertEquals(gameService.getAllGames()[0].javaClass, GameDto::class.java)
    }

    @Transactional
    @Test
    fun shouldSaveMultipleGamesThenReturnDtos() {
        val config1 = mapOf<String, Any>("Level" to 0)
        val config2 = mapOf<String, Any>("Level" to 3, "Ability" to "Gf")
        val game1 = GameEntity(
            name = "TestGame1", description = "test_game_description1", icon="test1", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true, url = "test_game_url1", configDescription = config1)
        val game2 = GameEntity(
            name = "TestGame2", description = "test_game_description2", icon="test2", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true, url = "test_game_url2", configDescription = config2)
        gameRepository.save(game1)
        gameRepository.save(game2)
        val games = gameService.getAllGames()
        for (game in games) {
            assertEquals(game.javaClass, GameDto::class.java)
            println(game.icon)
        }
    }

    @Transactional
    @Test
    fun testFindGameDtoById() {
        val config1 = mapOf<String, Any>("Level" to 0)
        val config2 = mapOf<String, Any>("Level" to 3, "Ability" to "Gf")
        val game1 = GameEntity(
            name = "TestGame1", description = "test_game_description1", icon="test1", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true, url = "test_game_url1", configDescription = config1)
        val game2 = GameEntity(
            name = "TestGame2", description = "test_game_description2", icon="test2", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true, url = "test_game_url2", configDescription = config2)
        gameRepository.save(game1)
        gameRepository.save(game2)
        assertEquals(game1.name, gameService.getGameById(game1.id!!).get().name)
        assertEquals(game2.url, gameService.getGameById(game2.id!!).get().url)
    }

}