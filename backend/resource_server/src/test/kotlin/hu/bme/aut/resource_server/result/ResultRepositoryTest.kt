package hu.bme.aut.resource_server.result

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
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class ResultRepositoryTest(
    @Autowired private var resultRepository: ResultRepository,
    @Autowired private var testService: TestUtilsService

) {
    @BeforeEach
    fun init(){
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }
    @Transactional
    @Test
    fun shouldSaveGameplay(){
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        val gameplay = testService.createGamePlayResult(user)
        var saved = resultRepository.save(gameplay)
        saved = resultRepository.findById(saved.id!!).get()
        assertEquals(saved.result, gameplay.result)
        assertEquals(100, saved.result["time"])
        assertEquals(10, saved.result["correct"])
    }

    @Test
    fun multipleSaveShouldNotThrow(){
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        assertDoesNotThrow { resultRepository.saveAll(createGameplayResults(user))}
        assertDoesNotThrow { resultRepository.saveAll(createGameplayResults(user))}
    }
    @Test
    fun shouldFindGameplaysOfUser(){
        val user1 = testService.createUnsavedTestUser()
        val user2 = testService.createUnsavedTestUser().copy(username = "second_user")
        testService.saveUser(user1)
        testService.saveUser(user2)
        val resultList1 = createGameplayResults(user1)
        val resultList2 = createGameplayResults(user2)
        resultRepository.saveAll(resultList1)
        resultRepository.saveAll(resultList2)
        val savedOfUser1 = resultRepository.findAllByUser(user1)
        val savedOfUser2 = resultRepository.findAllByUser(user2)
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
        resultRepository.saveAll(createGameplayResults(user))
        val startOfSearchTime = LocalDateTime.now().minusSeconds(1)
        val foundList = resultRepository.findAllByUserAndTimestampBetween(user,startOfSearchTime, startOfSearchTime.plusSeconds(2))
        assertEquals(3, foundList.size)
        assertTrue(foundList.all { it.user.id == user.id })
        assertTrue(foundList.all { it.timestamp?.isAfter(startOfSearchTime) ?: false })
        val emptyResult = resultRepository.findAllByUserAndTimestampBetween(user,startOfSearchTime.minusSeconds(5), startOfSearchTime)
        assertEquals(0, emptyResult.size)
    }

    private fun createGameplayResults(user: UserEntity): List<ResultEntity>{
        val game = testService.createAndSaveRecommendedGame(user)
        val gameplayList = listOf<ResultEntity>(
            ResultEntity(
                result = mapOf(Pair("level", 1), Pair("score", 2)),
                user = user,
                recommendedGame = game,
                config = mutableMapOf<String, Any>("speed" to "slow")
            ),
            ResultEntity(
                result = mapOf(Pair("level", 2), Pair("time", 210)),
                user = user,
                recommendedGame = game,
                config = mutableMapOf<String, Any>()
            ),
            ResultEntity(
                result = mapOf(Pair("level", 3), Pair("score", 0)),
                user = user,
                recommendedGame = game,
                config = mutableMapOf<String, Any>("time_limit" to 100)
            )
        )
        return  gameplayList
    }
}