package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScoreCalculatorTest {
    private lateinit var game: GameEntity
    private lateinit var winGame: GameEntity
    private lateinit var result: ResultForCalculationEntity

    @BeforeEach
    fun init(){
        game = TestDataSource.createGameForTest()
        result = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            result = mutableMapOf(
                "passed" to true,
            ),
            config = game.configDescription
        )
        winGame = TestDataSource.createGameForTest().copy(

        )
    }


    @Test
    fun shouldCalculateProperGameBasedOnWin(){
        val winResult = ResultForCalculationEntity(
            game = winGame,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            result = mutableMapOf("passed" to true),
            config = winGame.configItems.associate { it.paramName to it.initialValue }.toMutableMap()
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(winResult), winGame)
        assertEquals(1, normalizedResults.size)
        //param1: 10000/15000
        //param2: 5/10
        //result: (2/3+1/2)/2 = 7/12
        val expectedResult = "0.58333"
        assertEquals(expectedResult, normalizedResults[0].normalizedResult.toString().substring(0, 7))
    }

    @Test
    fun shouldNotThrowWhenMissingProperty(){
        val malformedResult = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            //maxHealthPoints is missing
            result = mutableMapOf(
                "level" to "5",
                "round" to "8",
                "healthPoints" to "5",
                "maxRound" to "10",
            ),
            config = game.configDescription
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(malformedResult), game)
        assertEquals(0, normalizedResults.size)
    }

    @Test
    fun shouldNotThrowWhenMalformedResult(){
        val malformedResult = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            result = mutableMapOf(
                "level" to "5",
                "round" to "8",
                "healthPoints" to "5",
                "maxRound" to "10",
                "maxHealthPoints" to "mistake",
            ),
            config = game.configDescription
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(malformedResult), game)
        assertEquals(0, normalizedResults.size)
    }

}