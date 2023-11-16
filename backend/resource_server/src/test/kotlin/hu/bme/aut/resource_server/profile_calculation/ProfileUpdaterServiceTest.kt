package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile_calculation.data.MeanAndDeviation
import hu.bme.aut.resource_server.profile_calculation.service.UserProfileUpdaterService
import kotlinx.coroutines.runBlocking
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


    @Test
    fun shouldUpdateOneAbilityWithoutError(){
        testService.saveAbility(TestDataSource.affectedAbility)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val user1 = TestDataSource.createNthUserWithAbilities(1, listOf(TestDataSource.affectedAbility), listOf(1.0))
        val user2 = TestDataSource.createNthUserWithAbilities(2, listOf(TestDataSource.affectedAbility), listOf(1.4))
        testService.saveUsers(listOf(user1, user2))
        val result1 = TestDataSource.createNormalizedResultForUser(user1,  50.0, game)
        val result2 = TestDataSource.createNormalizedResultForUser(user2,  52.0, game)
        testService.saveResults(listOf(result1, result2))
        runBlocking {
            service.updateUserProfileByResultsOfGame(game.id!!, MeanAndDeviation(26.0, 1.0))
        }
    }
}