package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.RoleName
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
class RecommendedGameRepositoryTest(
        @Autowired private var recommendedGameRepository: RecommendedGameRepository,
        @Autowired  private var userRepository: UserRepository,
        @Autowired  private var testUtilsService: TestUtilsService
) {

    @BeforeEach
    fun emptyRepo() {
        recommendedGameRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Transactional
    @Test
    fun shouldSaveRecommendedGame() {
        val testUser = UserEntity(
                firstName = "Test",
                lastName = "User",
                username = "test_user",
                roles = mutableSetOf(Role(roleName = RoleName.STUDENT)),
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
        )
        testUtilsService.saveUser(testUser)
        val testGame = testUtilsService.createAndSaveGame()
        val recommendedGame = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), level = 3, recommender = testUser, recommendedTo = testUser, game = testGame)
        val savedRecommendedGame = recommendedGameRepository.save(recommendedGame)
        assertNotNull(savedRecommendedGame.id)
    }

    @Transactional
    @Test
    fun shouldSaveThenDeleteRecommendedGame() {
        val testUser = UserEntity(
                firstName = "Test",
                lastName = "User",
                username = "test_user",
                roles = mutableSetOf(Role(roleName = RoleName.STUDENT)),
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
        )
        testUtilsService.saveUser(testUser)
        val testGame = testUtilsService.createAndSaveGame()
        val recommendedGame = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), level = 3, recommender = testUser, recommendedTo = testUser, game = testGame)
        val savedRecommendedGame = recommendedGameRepository.save(recommendedGame)
        assertNotNull(savedRecommendedGame.id)
        assertEquals(savedRecommendedGame.id, recommendedGame.id)
        assertEquals(recommendedGameRepository.findAllByRecommendedTo(testUser).toList().size, 1)
        assertEquals(recommendedGameRepository.findAll().toList().size, 1)
        recommendedGameRepository.deleteById(recommendedGame.id!!)
        assertEquals(recommendedGameRepository.findAllByRecommendedTo(testUser).toList().size, 0)
        assertEquals(recommendedGameRepository.findAll().toList().size, 0)
    }

}