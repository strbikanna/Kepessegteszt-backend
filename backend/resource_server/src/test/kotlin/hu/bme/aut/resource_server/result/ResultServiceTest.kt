package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.ProfileUpdateItem
import hu.bme.aut.resource_server.profile_calculation.TestDataSource
import hu.bme.aut.resource_server.recommendation.XPCalculator
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.utils.RoleName
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ResultServiceTest(
) {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var resultRepository: ResultRepository

    @Mock
    private lateinit var recommendedGameRepository: RecommendedGameRepository

    @InjectMocks
    private lateinit var resultService: ResultService


    @Test
    fun shouldSaveResultAndUpdateXP() {
        val recommendedGame = RecommendedGameEntity(
            id = 1,
            recommendedTo = testUser,
            config = mutableMapOf("difficulty" to "medium"),
            profileUpdateItems = setOf(),
            game = TestDataSource.createGameForTest()
        )
        val resultDto = ResultDto(
            gameplayId = 1,
            result = mapOf("score" to 80, "passed" to true),
            newConfig = null
        )

        `when`(recommendedGameRepository.findById(1)).thenReturn(Optional.of(recommendedGame))
        `when`(resultRepository.save(any(ResultEntity::class.java)))
            .thenAnswer { invocation -> invocation.getArgument(0) }
        `when`(userRepository.save(any(UserEntity::class.java)))
            .thenAnswer { invocation -> invocation.getArgument(0) }

        val savedResult = resultService.save(resultDto)

        assertTrue(savedResult.passed)
        assertEquals(
            testUser.xP + XPCalculator.calculateXP(recommendedGame.game.configItems, savedResult.config),
            savedResult.user.xP
        )
    }

    @Test
    fun profileUpdateShouldIncreaseAccuracyUpToMax() {
        testUser.profileFloat.add(
            FloatProfileItem(
                ability = TestDataSource.affectedAbility,
                abilityValue = 0.75,
                abilityAccuracy = 0.99
            )
        )
        val recommendedGame = RecommendedGameEntity(
            id = 1,
            recommendedTo = testUser,
            config = mutableMapOf("difficulty" to "medium"),
            profileUpdateItems = setOf(
                ProfileUpdateItem(
                    ability = TestDataSource.affectedAbility,
                    updatedValue = 0.8,
                    validOnSuccess = true
                ),
                ProfileUpdateItem(
                    ability = TestDataSource.affectedAbility,
                    updatedValue = 0.7,
                    validOnSuccess = false
                )
            ),
            game = TestDataSource.createGameForTest()
        )
        val resultDto = ResultDto(
            gameplayId = 1,
            result = mapOf("score" to 80, "passed" to true),
            newConfig = null
        )

        `when`(recommendedGameRepository.findById(1)).thenReturn(Optional.of(recommendedGame))

        resultService.updateProfileByResult(resultDto)

        var updatedProfileItem = testUser.profileFloat.find { it.ability.code == TestDataSource.affectedAbility.code }
        assertNotNull(updatedProfileItem)
        assertEquals(0.8, updatedProfileItem!!.abilityValue)
        assertEquals(1.0, updatedProfileItem.abilityAccuracy)

        resultService.updateProfileByResult(resultDto)
        updatedProfileItem = testUser.profileFloat.find { it.ability.code == TestDataSource.affectedAbility.code }
        assertNotNull(updatedProfileItem)
        assertEquals(0.8, updatedProfileItem!!.abilityValue)
        assertEquals(1.0, updatedProfileItem.abilityAccuracy)
    }

    @Test
    fun shouldGetResultsByAllFilter(){
        val gameIds = listOf(1, 2)
        val resultWin = true
        val userNames = listOf(testUser.username)
        val page = Pageable.ofSize(10).withPage(0)

        `when`(userRepository.findAllByUsernameIn(userNames)).thenReturn(listOf(testUser))
        `when`(resultRepository.findAllByUserInAndPassedAndRecommendedGameGameIdIn(
            listOf(testUser),
            resultWin,
            gameIds,
            page
        )).thenReturn(PageImpl(
            createResults(),
            PageRequest.of(0, 10),
            2
        ))

        resultService.getAllFiltered(userNames, gameIds, resultWin, page)
    }

    @Test
    fun shouldGetResultsByGameFilter(){
        val gameIds = listOf(1, 2)
        val userNames = listOf(testUser.username)
        val page = Pageable.ofSize(10).withPage(0)

        `when`(userRepository.findAllByUsernameIn(userNames)).thenReturn(listOf(testUser))
        `when`(resultRepository.findAllByUserInAndRecommendedGameGameIdIn(
            listOf(testUser),
            gameIds,
            page
        )).thenReturn(PageImpl(createResults(), PageRequest.of(0, 10), 2))

        resultService.getAllFiltered(userNames, gameIds, null, page)
    }

    @Test
    fun shouldGetResultsByFilter(){
        val resultWin = true
        val userNames = listOf(testUser.username)
        val page = Pageable.ofSize(10).withPage(0)

        `when`(userRepository.findAllByUsernameIn(userNames)).thenReturn(listOf(testUser))
        `when`(resultRepository.findAllByUserInAndPassed(
            listOf(testUser),
            resultWin,
            page
        )).thenReturn(PageImpl(createResults(), PageRequest.of(0, 10), 2))

        resultService.getAllFiltered(userNames, null, resultWin, page)
    }

    @Test
    fun shouldGetResultsByFilter_2(){
        val gameIds = listOf(1, 2)
        val userNames = listOf(testUser.username)
        val page = Pageable.ofSize(10).withPage(0)

        `when`(resultRepository.findAllByRecommendedGameGameIdIn(
            listOf(1,2), page
        )).thenReturn(PageImpl(createResults(), PageRequest.of(0, 10), 2))

        resultService.getAllFiltered(gameIds, null, page)
    }

    private var testUser = UserEntity(
        id = 1,
        username = "testuser",
        firstName = "Test",
        lastName = "User",
        roles = mutableSetOf(Role(RoleName.STUDENT)),
        xP = 100,
    )

    private fun createResults(): List<ResultEntity> =
        listOf(
            ResultEntity(
                id = 1,
                result = mapOf("score" to 80, "passed" to true),
                passed = true,
                config = mutableMapOf("difficulty" to "medium"),
                user = testUser,
                timestamp = LocalDateTime.now(),
                recommendedGame = RecommendedGameEntity(
                    id = 1,
                    recommendedTo = testUser,
                    config = mutableMapOf("difficulty" to "medium"),
                    profileUpdateItems = setOf(),
                    game = TestDataSource.createGameForTest()
                )
            ),
            ResultEntity(
                id = 2,
                result = mapOf("score" to 50, "passed" to false),
                passed = false,
                config = mutableMapOf("difficulty" to "hard"),
                user = testUser,
                timestamp = LocalDateTime.now(),
                recommendedGame = RecommendedGameEntity(
                    id = 2,
                    recommendedTo = testUser,
                    config = mutableMapOf("difficulty" to "hard"),
                    profileUpdateItems = setOf(),
                    game = TestDataSource.createGameForTest()
                )
            )
        )




}