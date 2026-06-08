package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.RoleName
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["cognitive-app.resource-server.security.bypass=true"]
)
@ActiveProfiles("test")
class GameControllerTest(
    @Autowired private var testService: TestUtilsService,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository
) {
    @LocalServerPort
    private var port: Int? = null

    private lateinit var requestSpec: RequestSpecification

    private val gameEndpoint = "/game"

    @BeforeEach
    fun init() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        testService.createAndSaveGame()
        requestSpec = RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(port!!)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .addFilter(RequestLoggingFilter())
            .addFilter(ResponseLoggingFilter())
            .build()
    }

    @Test
    fun shouldSaveWithCorrectAuthentication(){
        testService.saveAuthUserWithRights(RoleName.STUDENT)

        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .get("$gameEndpoint/all")
            .then().statusCode(HttpStatus.OK.value())
            .body("", hasSize<GameDto>(1))
    }

    @Test
    fun shouldFindByNameCaseInsensitive() {
        testService.saveAuthUserWithRights(RoleName.STUDENT)
        gameRepository.save(
            GameEntity(
                modelId = "model-test",
                version = 1,
                name = "Best Game",
                description = "desc",
                thumbnailPath = "thumb.png",
                active = true,
                affectedAbilities = mutableSetOf()
            )
        )
        gameRepository.save(
            GameEntity(
                modelId = "model-test2",
                version = 1,
                name = "Easy Game",
                description = "desc",
                thumbnailPath = "thumb.png",
                active = true,
                affectedAbilities = mutableSetOf()
            )
        )

        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .get("$gameEndpoint/search?name=eSt")
            .then().statusCode(HttpStatus.OK.value())
            .body("", hasSize<GameDto>(2))
    }

    @Test
    fun createGame_createsRecommendationsForAllUsers() {
        testService.saveUser(
            testService.createUnsavedTestUser().copy(username = "user2")
        )
        testService.saveUser(
            testService.createUnsavedTestUser().copy(username = "user3")
        )
        val gamesBefore = gameRepository.count()

        testService.saveAuthUserWithRights(RoleName.ADMIN)
        val newGame = GameDto(
            id = null,
            modelId = "model-new",
            version = 1,
            name = "CreateGameTest",
            description = "desc",
            thumbnailPath = "thumb.png",
            active = true,
            affectedAbilities = mutableSetOf(),
            configItems = mutableSetOf(ConfigItem(
                paramName = "param1",
                initialValue = 1,
                description = "desc",
                increment = 1,
                easiestValue = 0,
                hardestValue = 10,
                maxAbilityEffect = 1.0
            )
        ))

        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(newGame)
            .post(gameEndpoint)
            .then().statusCode(HttpStatus.CREATED.value())

        val gamesAfter = gameRepository.count()
        assert(gamesAfter == gamesBefore + 1)

        val savedGame = gameRepository.findGameByName(newGame.name)
        assertTrue(savedGame.isPresent)
        assertEquals(newGame.modelId, savedGame.get().modelId)

        val recsAfter = recommendedGameRepository.count()
        assertEquals(3, recsAfter)
    }

    @Test
    fun deleteGame_removesRecommendationsForGame() {

        testService.saveAuthUserWithRights(RoleName.ADMIN)
        val newGame = GameEntity(
            id = null,
            modelId = "model-delete",
            version = 1,
            name = "DeleteGameTest",
            description = "desc",
            thumbnailPath = "thumb.png",
            active = true,
            affectedAbilities = mutableSetOf(testService.abilityGv),
            configItems = mutableSetOf(ConfigItem(
                paramName = "param1",
                initialValue = 1,
                description = "desc",
                increment = 1,
                easiestValue = 0,
                hardestValue = 10,
                maxAbilityEffect = 1.0
            ))
        )

        val gameSaved = gameRepository.save(newGame)
        val gamesBefore = gameRepository.count()
        val abilitiesBefore = recommendedGameRepository.count()

        recommendedGameRepository.save(
            RecommendedGameEntity(
                game = gameSaved,
                recommendedTo = testService.saveUser(
                    testService.createUnsavedTestUser().copy(username = "user2")
                ),
                config = emptyMap()
            )
        )
        recommendedGameRepository.save(
            RecommendedGameEntity(
                game = gameSaved,
                recommendedTo = testService.saveUser(
                    testService.createUnsavedTestUser().copy(username = "user3")
                ),
                config = emptyMap()
            )
        )

        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .delete(gameEndpoint + "/${gameSaved.id!!}")
            .then().statusCode(HttpStatus.OK.value())

        val gamesAfter = gameRepository.count()
        assert(gamesAfter == gamesBefore - 1)

        val recsAfter = recommendedGameRepository.count()
        assertEquals(0, recsAfter)

        val abilitiesAfter = recommendedGameRepository.count()
        assertEquals(abilitiesBefore, abilitiesAfter)
    }
}