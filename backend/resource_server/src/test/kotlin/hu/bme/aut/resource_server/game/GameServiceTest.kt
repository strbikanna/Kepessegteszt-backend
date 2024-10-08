package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.TestUtilsService
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.nio.file.Paths

@ActiveProfiles("test")
@SpringBootTest
class GameServiceTest(
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var gameService: GameService,
    @Autowired private var testUtilsService: TestUtilsService
) {
    @BeforeEach
    fun emptyRepo() {
        gameRepository.deleteAll()
    }

    @Transactional
    @Test
    fun testFindGameById() {
        val config1 = mutableMapOf<String, Any>("Level" to 0)
        val config2 = mutableMapOf<String, Any>("Level" to 3, "Ability" to "Gf")
        val game1 = GameEntity(
            version = 1, name = "TestGame1", description = "test_game_description1", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true,  configDescription = config1, affectedAbilities = mutableSetOf(testUtilsService.abilityColorsense))
        val game2 = GameEntity(
            version = 1, name = "TestGame2", description = "test_game_description2", thumbnailPath = Paths.get("src", "test", "kotlin/hu/bme/aut/resource_server", "game", "resources", "test_game_icon.png").toString(),
            active = true,  configDescription = config2, affectedAbilities = mutableSetOf(testUtilsService.abilityGf, testUtilsService.abilityGq))
        gameRepository.save(game1)
        gameRepository.save(game2)
        assertEquals(game1.name, gameService.getGameById(game1.id!!).get().name)
    }

}