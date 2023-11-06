package hu.bme.aut.resource_server.calculation

import hu.bme.aut.resource_server.TestUtilsService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    @Test
    fun testMeanAndDeviationCalculation(){
        testService.saveAbility(TestDataSource.affectedAbility)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val users = TestDataSource.createUsersForTest(30)
        testService.saveUsers(users)
        val results = TestDataSource.createResultsForCalculation_ThreePartition(users, game)
        repository.saveAll(results)
        val meanAndDeviation = service.processGameResults(game)
        assertTrue(meanAndDeviation.mean > 41.5 && meanAndDeviation.mean < 41.6)
        assertTrue(meanAndDeviation.deviation > 2.7 && meanAndDeviation.deviation < 2.8)
    }

}