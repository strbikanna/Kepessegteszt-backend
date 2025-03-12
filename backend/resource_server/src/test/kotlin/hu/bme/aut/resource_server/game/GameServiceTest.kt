package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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

    private lateinit var game1: GameEntity
    private lateinit var game2: GameEntity

    @BeforeEach
    fun emptyRepo() {
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
        gameRepository.deleteAll()
        initRepo()
    }

    fun initRepo() {
        game1 = GameEntity(
            version = 1,
            name = "TestGame1",
            description = "test_game_description1",
            thumbnailPath = Paths.get(
                "src",
                "test",
                "kotlin/hu/bme/aut/resource_server",
                "game",
                "resources",
                "test_game_icon.png"
            ).toString(),
            active = true,
            affectedAbilities = mutableSetOf(testUtilsService.abilityColorsense)
        )
        game2 = GameEntity(
            version = 1,
            name = "TestGame2",
            description = "test_game_description2",
            thumbnailPath = Paths.get(
                "src",
                "test",
                "kotlin/hu/bme/aut/resource_server",
                "game",
                "resources",
                "test_game_icon.png"
            ).toString(),
            active = true,
            affectedAbilities = mutableSetOf(testUtilsService.abilityGf, testUtilsService.abilityGq)
        )
        gameRepository.save(game1)
        gameRepository.save(game2)
    }

    @Transactional
    @Test
    fun testFindGameById() {
        assertEquals(game1.name, gameService.getGameById(game1.id!!).get().name)
    }

    @Test
    fun testUpdateGame() {
        val updatedGame = game1.copy(name = "UpdatedGame")
        gameService.updateGame(updatedGame)
    }

    @Test
    fun testUpdateGameDifferentConfig() {
        val user = testUtilsService.createUnsavedTestUser()
        testUtilsService.saveUser(user)
        val recommendation = RecommendedGameEntity(
            game = game1,
            recommendedTo = user,
            config = mapOf(),
            completed = false
        )
        testUtilsService.recommendedGameRepository.save(recommendation)
        assertEquals(1, testUtilsService.recommendedGameRepository.findAll().size)
        val newConfigItem = ConfigItem(
            initialValue = 0,
            hardestValue = 10,
            easiestValue = 0,
            paramName = "Level",
            description = "Level of the game",
            increment = 1,
            maxAbilityEffect = 1
        )
        val updatedGame = game1.copy(configItems = mutableSetOf(newConfigItem), name="UpdatedGame")
        gameService.updateGame(updatedGame)
        val allRecommendations = testUtilsService.recommendedGameRepository.findAll()
        assertTrue(allRecommendations.none { it.game == game1 })
        assertTrue(allRecommendations.any { it.game.name == updatedGame.name })
    }

}