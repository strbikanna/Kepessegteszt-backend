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
                "level" to 5,
                "round" to 8,
                "healthPoints" to 5,
                "maxRound" to 10,
                "maxHealthPoints" to 6,
            ),
            config = game.configDescription
        )
        winGame = TestDataSource.createGameForTest().copy(
            configDescription = mutableMapOf(
                "levelFieldName" to "level",
                "winFieldName" to "gameWon",
                "maxLevel" to 10,
            )
        )
    }


    @Test
    fun shouldCalculateProperGameBasedOnPoints(){
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(result), game)
        assertEquals(1, normalizedResults.size)
        val expectedResult = (5 * 2.0 /10 + 8.0/10 + 5.0/6)/4
        assertEquals(expectedResult, normalizedResults[0].normalizedResult)
    }

    @Test
    fun shouldReturnPointsCorrectly(){
        val maxPointsOfLevel = ScoreCalculator.getMaxScoreOfLevel(result, game)
        val actualPoints = ScoreCalculator.getActualScoreOfLevel(result, game)
        assertEquals(16.0, maxPointsOfLevel)
        assertEquals(13.0, actualPoints)
    }
    @Test
    fun shouldCalculateProperGameBasedOnWin(){
        val winResult = ResultForCalculationEntity(
            game = winGame,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            result = mutableMapOf(
                "level" to 5,
                "gameWon" to true,
            ),
            config = winGame.configDescription
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(winResult), winGame)
        assertEquals(1, normalizedResults.size)
        val expectedResult = (5 /10  + 1.0)/2
        assertEquals(expectedResult, normalizedResults[0].normalizedResult)
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
    fun shouldNotThrowWhenMalformedResult1(){
        val malformedResult = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            //maxHealthPoints is malformed
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
    @Test
    fun shouldNotThrowWhenMalformedResult2(){
        val malformedResult = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            //maxExtraPointsField is not as in config
            result = mutableMapOf(
                "level" to "5",
                "round" to "8",
                "healthPoints" to "5",
                "maxRound" to "10",
                "MISTAKE" to "3",
            ),
            config = game.configDescription
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(malformedResult), game)
        assertEquals(0, normalizedResults.size)
    }
    @Test
    fun shouldCalculateWithoutExtraPoints(){
        val malformedResult = ResultForCalculationEntity(
            game = game,
            user = TestDataSource.createUsersForTestWithEmptyProfile(1)[0],
            //maxPoints are missing
            result = mutableMapOf(
                "level" to "5",
                "round" to "8",
                "maxRound" to "10",
            ),
            config = game.configDescription
        )
        val normalizedResults = ScoreCalculator.calculateNormalizedScores(listOf(malformedResult), game)
        assertEquals(1, normalizedResults.size)
        val expectedResult = (5*2/10 + 8.0/10)/4
        assertEquals(expectedResult, normalizedResults[0].normalizedResult)
    }
}