package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.utils.RoleName
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties= ["cognitive-app.resource-server.security.bypass=true"]
)
@ActiveProfiles("test")
class ResultControllerTest(
    @Autowired private var testService: TestUtilsService,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
) {

    @LocalServerPort
    private var port: Int? = null

    private lateinit var requestSpec: RequestSpecification

    private val gameplayEndpoint = "/gameplay"
    @BeforeEach
    fun init(){
        testService.emptyRepositories()
        testService.fillAbilityRepository()
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
    fun shouldReturnForbiddenWithoutCorrectAuthentication(){
        val authUser = testService.saveAuthUserWithRights(RoleName.STUDENT)
        val game = testService.createAndSaveRecommendedGame(authUser)
        val gameplayData = ResultDto(result= mapOf(), gameplayId = game.id!! )
        given(requestSpec)
            .header(testService.authHeaderName, "random user")
            .body(gameplayData)
            .post(gameplayEndpoint)
            .then().statusCode(HttpStatus.FORBIDDEN.value())

    }
    @Test
    fun shouldSaveWithCorrectAuthentication(){
        val user = testService.saveAuthUserWithRights(RoleName.STUDENT)
        val game = testService.saveAuthGame()
        val recommendedGame = createAndSaveRecommendedGameForAuth(user, game)
        val result = mapOf(Pair("success", true))
        val gameplayData = ResultDto(result= result,  gameplayId = recommendedGame.id!! )
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .header(testService.gameAuthHeaderName, testService.authGameId)
            .body(gameplayData)
            .post(gameplayEndpoint)
            .then().statusCode(HttpStatus.CREATED.value())
    }
    fun createAndSaveRecommendedGameForAuth(user: UserEntity, game: GameEntity): RecommendedGameEntity {
        val recommendedGameEntity = RecommendedGameEntity(
            game = game,
            recommendedTo = user,
            config = mapOf(),
        )
        return recommendedGameRepository.save(recommendedGameEntity)
    }
}