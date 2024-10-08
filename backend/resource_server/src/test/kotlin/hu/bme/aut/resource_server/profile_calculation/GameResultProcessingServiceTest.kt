package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationRepository
import hu.bme.aut.resource_server.profile_calculation.service.GameResultProcessingService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class GameResultProcessingServiceTest(
    @Autowired private var service: GameResultProcessingService,
    @Autowired private var repository: ResultForCalculationRepository,
    @Autowired private var testService: TestUtilsService
) {
    @BeforeEach
    fun init() {
        testService.emptyRepositories()
        repository.deleteAll()
        testService.fillAbilityRepository()
    }

    @Test
    fun testMeanAndDeviationCalculation() {
        testService.saveAbility(TestDataSource.affectedAbility)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val users = TestDataSource.createUsersForTestWithEmptyProfile(30)
        testService.saveUsers(users)
        val results = TestDataSource.createResultsForCalculation_ThreePartition(users, game)
        repository.saveAll(results)
        runBlocking {
            val meanAndDeviation = service.processGameResults(game.id!!)
            assertEquals(0.0, meanAndDeviation.mean) //because result difficulty is not set
        }

    }

    @Test
    fun shouldOnlyLeaveNormalizedResultsInRepository() {
        testService.saveAbility(TestDataSource.affectedAbility)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val users = TestDataSource.createUsersForTestWithEmptyProfile(10)
        testService.saveUsers(users)
        val results = TestDataSource.createResultsForCalculation_ThreePartition(users, game)
        repository.saveAll(results)
        runBlocking {
            service.processGameResults(game.id!!)
            val allResults = repository.findAll()
            assertTrue(allResults.all { it.normalizedResult != null })
            assertEquals(10,  allResults.size)
            assertEquals(allResults.size,  allResults.distinctBy { it.user.id }.size)
        }
    }

}