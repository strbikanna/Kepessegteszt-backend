package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AbilityRateCalculatorServiceTest(
    @Autowired private val abilityRateCalculatorService: AbilityRateCalculatorService,
    @Autowired private var testService: TestUtilsService
) {
    @BeforeEach
    fun init(){
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        testService.saveAbility(TestDataSource.affectedAbility)
    }

    @Test
    fun shouldConstructArraysCorrectly(){
        val ability2 = testService.abilityGq
        val ability3 = testService.abilityGsm
        val ability1 = testService.abilityGf
        val user1 = TestDataSource.createNthUserWithAbilities(1, listOf(ability1, ability2, ability3), listOf(1.0, 0.9, 0.7))
        val user2 = TestDataSource.createNthUserWithAbilities(2, listOf(ability1, ability2, ability3), listOf(0.7, 0.8, 0.9))
        val user3 = TestDataSource.createNthUserWithAbilities(3, listOf(ability1, ability2, ability3), listOf(0.9, 0.8, 0.7))
        val user4 = TestDataSource.createNthUserWithAbilities(4, listOf(ability1, ability2, ability3), listOf(1.1,1.2, 1.15))
        val users = listOf( user1, user2, user3, user4 )
        testService.saveUsers(users)
        val game = TestDataSource.createGameForTest()
        testService.saveGame(game)
        val results = listOf(
            TestDataSource.createNormalizedResultForUser(user1, 0.7, game),
            TestDataSource.createNormalizedResultForUser(user2, 0.8, game),
            TestDataSource.createNormalizedResultForUser(user3, 0.67, game),
            TestDataSource.createNormalizedResultForUser(user4, 0.9, game),
        )
        testService.saveResults(results)
        val (abilityValues, resultValues) = abilityRateCalculatorService.getAbilityValuesAndValuesFromResultsStructured(results, listOf(ability1, ability2, ability3))
        assertTrue(abilityValues.contains(listOf(1.0, 0.9, 0.7)))
        assertTrue(abilityValues.contains(listOf(0.7, 0.8, 0.9)))
        assertTrue(abilityValues.contains(listOf(0.9, 0.8, 0.7)))
        assertTrue(abilityValues.contains(listOf(1.1,1.2, 1.15)))
        assertEquals(4, abilityValues.size)
        assertEquals(4, resultValues.size)
        assertEquals(resultValues.indexOf(0.7), abilityValues.indexOf(listOf(1.0, 0.9, 0.7)))
        assertEquals(resultValues.indexOf(0.8), abilityValues.indexOf(listOf(0.7, 0.8, 0.9)))
        assertEquals(resultValues.indexOf(0.67), abilityValues.indexOf(listOf(0.9, 0.8, 0.7)))
        assertEquals(resultValues.indexOf(0.9), abilityValues.indexOf(listOf(1.1,1.2, 1.15)))
    }
}