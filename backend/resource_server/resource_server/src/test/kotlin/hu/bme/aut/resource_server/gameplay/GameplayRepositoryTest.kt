package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.user.UserEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import java.time.Month

@SpringBootTest
@ActiveProfiles("test")
class GameplayRepositoryTest(
    @Autowired private var gameplayRepository: GameplayRepository,
    @Autowired private var testService: TestUtilsService

) {
    @BeforeEach
    fun init(){
        gameplayRepository.deleteAll()
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }
    @Test
    fun shouldSaveGameplay(){
        val gameplay = testService.createGamePlay()
        gameplayRepository.save(gameplay)

    }

    @Test
    fun multipleSaveShouldNotThrow(){
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        assertDoesNotThrow { gameplayRepository.saveAll(createGameplays(user))}
        assertDoesNotThrow { gameplayRepository.saveAll(createGameplays(user))}
    }
    @Test
    fun shouldFindGameplaysOfUser(){
        val user1 = testService.createUnsavedTestUser()
        val user2 = testService.createUnsavedTestUser().copy(username = "second_user")
        val gameList1 = createGameplays(user1)
        val gameList2 = createGameplays(user2)
        testService.saveUser(user1)
        testService.saveUser(user2)
        gameplayRepository.saveAll(gameList1)
        gameplayRepository.saveAll(gameList2)
        val savedOfUser1 = gameplayRepository.findAllByUser(user1)
        val savedOfUser2 = gameplayRepository.findAllByUser(user2)
        assertEquals(3, savedOfUser1.size)
        assertEquals(3, savedOfUser2.size)
        assertTrue(savedOfUser1.all { it.user.id == user1.id })
        assertTrue(savedOfUser2.all { it.user.id == user2.id })
        assertTrue(savedOfUser2.all { it.timestamp != null })
        assertTrue(savedOfUser1.all { it.timestamp != null })
        assertTrue(savedOfUser1.all { it.timestamp?.isBefore(LocalDateTime.now().plusSeconds(1)) ?: false })
        assertTrue(savedOfUser2.all { it.timestamp?.isBefore(LocalDateTime.now().plusSeconds(1)) ?: false })
    }

    @Test
    fun shouldFindAllByUserAndTime(){
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        gameplayRepository.saveAll(createGameplays(user))
        val startOfSearchTime = LocalDateTime.of(2023,Month.OCTOBER,10, 10, 10)
        val foundList = gameplayRepository.findAllByUserAndTimestampBetween(user,startOfSearchTime)
        assertEquals(3, foundList.size)
        assertTrue(foundList.all { it.user.id == user.id })
        assertTrue(foundList.all { it.timestamp?.isAfter(startOfSearchTime) ?: false })
        val emptyResult = gameplayRepository.findAllByUserAndTimestampBetween(user,startOfSearchTime, startOfSearchTime.plusDays(1),)
        assertEquals(0, emptyResult.size)
    }

    private fun createGameplays(user: UserEntity): List<GamePlay>{
        val game = testService.createAndSaveGame()
        val gameplayList = listOf<GamePlay>(
            GamePlay(
                result = mapOf(Pair("level", 1), Pair("score", 2)),
                user = user,
                game = game
            ),
            GamePlay(
                result = mapOf(Pair("level", 2), Pair("time", 210)),
                user = user,
                game = game
            ),
            GamePlay(
                result = mapOf(Pair("level", 3), Pair("score", 0)),
                user = user,
                game = game
            )
        )
        return  gameplayList
    }
}