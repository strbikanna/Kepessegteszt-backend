package hu.bme.aut.resource_server.profile_calculation.data

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * Tests procedure call, unfortunately does not work with in-memory database.
 * If real MySQL database is used and called procedure is present, then it works.
 */
@SpringBootTest
@ActiveProfiles("test")
class ResultForCalculationDataServiceTest(
    @Autowired private var resultForCalculationDataService: ResultForCalculationDataService,
    @Autowired private var testService: TestUtilsService
) {
    private var gameId = 1

    @BeforeEach
    fun emptyDb(){
        testService.emptyRepositories()
        val testGame = TestDataSource.createGameForTest()
        testService.saveAbility(TestDataSource.affectedAbility)
        gameId = testService.saveGame(testGame).id!!
        val testUsers = TestDataSource.createUsersForTestWithEmptyProfile(10)
        testUsers.forEach {
            it.profileFloat.add(
                FloatProfileItem(
                    ability = TestDataSource.affectedAbility,
                    abilityValue = Math.random() * 1.3
                )
            )
        }
        testService.saveUsers(testUsers)
    }

}