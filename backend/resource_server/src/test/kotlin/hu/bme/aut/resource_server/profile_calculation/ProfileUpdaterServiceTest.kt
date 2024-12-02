package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile_calculation.data.MeanAndDeviation
import hu.bme.aut.resource_server.profile_calculation.service.UserProfileUpdaterService
import jakarta.transaction.Transactional
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ProfileUpdaterServiceTest(
    @Autowired private var service: UserProfileUpdaterService,
    @Autowired private var testService: TestUtilsService
) {

    @BeforeEach
    fun init() {
        testService.emptyRepositories()
    }

    @Test
    @Transactional
    fun shouldUpdateOneAbilityWithoutError() {
        testService.saveAbility(TestDataSource.affectedAbility)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val user1 = TestDataSource.createNthUserWithAbilities(1, listOf(TestDataSource.affectedAbility), listOf(1.0))
        val user2 = TestDataSource.createNthUserWithAbilities(2, listOf(TestDataSource.affectedAbility), listOf(1.4))
        testService.saveUsers(listOf(user1, user2))
        val result1 = TestDataSource.createNormalizedResultForUser(user1, 0.95, game)
        val result2 = TestDataSource.createNormalizedResultForUser(user2, 0.45, game)
        testService.saveResults(listOf(result1, result2))
        service.updateUserProfileByResultsOfGame(game.id!!, MeanAndDeviation(0.7, 0.25))
        //new value for user1: ((1 + (0.95 - 0.7) / 0.25 * 0.15) +1.0) /2 = 1.075
        //new value for user2: ((1 + (0.45 - 0.7) / 0.25 * 0.15) +1.4) /2 = 1.125
        val updatedUser1 = testService.userRepository.findById(user1.id!!).get()
        val updatedUser2 = testService.userRepository.findById(user2.id!!).get()
        assertEquals(
            1.075,
            updatedUser1.profileFloat.find { it.ability.code == TestDataSource.affectedAbility.code }?.abilityValue
        )
        assertEquals(
            1.125,
            updatedUser2.profileFloat.find { it.ability.code == TestDataSource.affectedAbility.code }?.abilityValue
        )


    }
}