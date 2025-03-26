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
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
    }

    @Transactional
    @Test
    fun shouldSaveGame() {
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, affectedAbilities = abilities)
        gameRepository.save(game)
        assertNotNull(game.id)
    }

    @Transactional
    @Test
    fun testFindByName() {
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, affectedAbilities = abilities)
        println(game.affectedAbilities.toString())
        gameRepository.save(game)
        assertNotNull(game.id)
        assertTrue(gameRepository.existsByName("TestGame"))
        assertEquals(Optional.of(game), gameRepository.findGameByName("TestGame"))
    }

    @Transactional
    @Test
    fun shouldSaveMultipleGames() {
        val abilities1 = mutableSetOf(testUtilsService.abilityColorsense)
        val abilities2 = mutableSetOf(testUtilsService.abilityGf, testUtilsService.abilityGq)

        val game1 = GameEntity(
            version = 1, name = "TestGame1", description = "test_game_description1", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game1",
                active = true,   affectedAbilities = abilities1)
        val game2 = GameEntity(
            version = 1, name = "TestGame2", description = "test_game_description2", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game2",
                active = true, affectedAbilities = abilities2)
        val savedGame1 = gameRepository.save(game1)
        val savedGame2 = gameRepository.save(game2)
        assertNotEquals(game1.id, game2.id)
        assertEquals(savedGame2.id, game2.id)
        assertEquals(gameRepository.findAll().toList().size, 2)
    }

    @Transactional
    @Test
    fun shouldSaveThenDeleteGame() {
        val abilities = mutableSetOf(testUtilsService.abilityColorsense)
        val game = GameEntity(
            version = 1, name = "TestGame", description = "test_game_description", thumbnailPath = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true,  affectedAbilities = abilities)
        gameRepository.save(game)
        assertNotNull(game.id)
        assertEquals(gameRepository.findAll().toList().size, 1)
        gameRepository.deleteById(game.id!!)
        assertEquals(gameRepository.findAll().toList().size, 0)
    }
}