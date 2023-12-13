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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        testUtilsService.emptyRepositories()
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
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser, recommendedTo = testUser, game = testGame)
        val savedRecommendedGame = recommendedGameRepository.save(recommendedGame)
        assertNotNull(savedRecommendedGame.id)
    }

    @Transactional
    @Test
    fun shouldSaveMultipleRecommendedGames() {
        val testUser = UserEntity(
                firstName = "Test",
                lastName = "User",
                username = "test_user",
                roles = mutableSetOf(Role(roleName = RoleName.TEACHER)),
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
        )
        val testUser2 = testUser.copy(lastName = "User2", username = "test_user2")
        val testUser3 = testUser.copy(lastName = "User3", username = "test_user3")
        testUtilsService.saveUser(testUser)
        testUtilsService.saveUser(testUser2)
        testUtilsService.saveUser(testUser3)
        val testGame = testUtilsService.createAndSaveGame()
        val testGame2 = testGame.copy(name = "Test game2", id = testGame.id!!+1)
        testUtilsService.gameRepository.save(testGame2)
        val recommendedGame1 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser2, recommendedTo = testUser, game = testGame)
        val savedRecommendedGame1 = recommendedGameRepository.save(recommendedGame1)
        val recommendedGame2 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser3, recommendedTo = testUser, game = testGame2)
        val savedRecommendedGame2 = recommendedGameRepository.save(recommendedGame2)
        val recommendedGame3 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser3, recommendedTo = testUser2, game = testGame)
        val savedRecommendedGame3 = recommendedGameRepository.save(recommendedGame3)
        assertNotNull(savedRecommendedGame1.id)
        assertNotNull(savedRecommendedGame2.id)
        assertNotNull(savedRecommendedGame3.id)
        assertNotEquals(savedRecommendedGame1.id, savedRecommendedGame2.id)
        assertEquals(recommendedGameRepository.findAllByRecommendedTo(testUser).size, 2)
    }

    @Transactional
    @Test
    fun testSortingAndPagingWithMultipleRecommendedGames() {
        val testUser = UserEntity(
                firstName = "Test",
                lastName = "User",
                username = "test_user",
                roles = mutableSetOf(Role(roleName = RoleName.TEACHER)),
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
        )
        val testUser2 = testUser.copy(lastName = "User2", username = "test_user2")
        val testUser3 = testUser.copy(lastName = "User3", username = "test_user3")
        testUtilsService.saveUser(testUser)
        testUtilsService.saveUser(testUser2)
        testUtilsService.saveUser(testUser3)
        val testGame = testUtilsService.createAndSaveGame()
        val testGame2 = testGame.copy(name = "Test game2", id = testGame.id!!+1)
        testUtilsService.gameRepository.save(testGame2)
        val recommendedGame1 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser2, recommendedTo = testUser, game = testGame)
        val savedRecommendedGame1 = recommendedGameRepository.save(recommendedGame1)
        val recommendedGame2 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser3, recommendedTo = testUser, game = testGame2)
        val savedRecommendedGame2 = recommendedGameRepository.save(recommendedGame2)
        val recommendedGame3 = RecommendedGameEntity(
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser3, recommendedTo = testUser2, game = testGame)
        val savedRecommendedGame3 = recommendedGameRepository.save(recommendedGame3)
        assertNotNull(savedRecommendedGame1.id)
        assertNotNull(savedRecommendedGame2.id)
        assertNotNull(savedRecommendedGame3.id)
        assertNotEquals(savedRecommendedGame1.id, savedRecommendedGame2.id)
        assertEquals(recommendedGameRepository.findAllByRecommendedTo(testUser).size, 2)
        assertNotNull(recommendedGameRepository.findAllByRecommendedTo(testUser2))
        assertEquals(testUser2, recommendedGameRepository.findAllSortedByRecommendedTo(testUser, Sort.by("recommender").ascending())[0].recommender)
        assertEquals(testUser2, recommendedGameRepository.findAllPagedByRecommendedTo(testUser, PageRequest.of(0, 1, Sort.by("recommender").ascending()))[0].recommender)
        assertEquals(testUser3, recommendedGameRepository.findAllPagedByRecommendedTo(testUser, PageRequest.of(1, 1, Sort.by("recommender").ascending()))[0].recommender)
        assertEquals(testUser3, recommendedGameRepository.findAllPagedByRecommendedTo(testUser, PageRequest.of(0, 2, Sort.by("recommender").ascending()))[1].recommender)
        assertEquals(1, recommendedGameRepository.findAllPagedByRecommendedTo(testUser, PageRequest.of(0, 1)).size)
        assertEquals(1, recommendedGameRepository.findAllPagedByRecommendedTo(testUser, PageRequest.of(1, 1)).size)
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
                timestamp = LocalDateTime.now(), config = mapOf("level" to 10), recommender = testUser, recommendedTo = testUser, game = testGame)
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