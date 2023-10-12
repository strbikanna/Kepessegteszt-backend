/*
package hu.bme.aut.resource_server.game


import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
@Ignore
class GameRepositoryTest(
    @Autowired private var gameRepository: GameRepository,
) {
    @BeforeEach
    fun emptyRepo() {
        gameRepository.deleteAll()
    }

    @Transactional
    @Test
    fun shouldSaveGame() {
        val game = GameEntity(
            name = "TestGame", description = "test_game_description", icon = "backend/resource_server/resource_server/src/test/kotlin/hu/bme/aut/resource_server/game_icons/test_game",
                active = true, url = "test_game_url", jsonDescriptor = "test_game_descriptor")
        gameRepository.save(game)
        assertNotNull(game.id)
        val savedGame = gameRepository.findById(game.id!!).get()
        assertNotNull(savedGame.id)
    }
}
 */