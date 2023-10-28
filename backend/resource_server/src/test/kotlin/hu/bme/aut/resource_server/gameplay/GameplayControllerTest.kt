package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.TestUtilsService
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GameplayControllerTest(
    @Autowired private var testService: TestUtilsService
) {

    @LocalServerPort
    private var port: Int? = null

    private lateinit var requestSpec: RequestSpecification

    private val gameplayEndpoint = "/gameplay"
    @BeforeEach
    fun init(){
        testService.emptyRepositories()
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
        testService.saveAuthUserWithRights(RoleName.STUDENT)
        val game = testService.createAndSaveGame()
        val gameplayData = GameplayResultDto(gameResult= mapOf(), username = testService.authUsername, gameId = game.id!! )
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(gameplayData)
            .post(gameplayEndpoint)
            .then().statusCode(HttpStatus.FORBIDDEN.value())

    }
    @Test
    fun shouldSaveWithCorrectAuthentication(){
        testService.saveAuthUserWithRights(RoleName.STUDENT)
        testService.saveAuthGame()
        val result = mapOf(Pair("success", true))
        val gameplayData = GameplayResultDto(gameResult= result, username = testService.authUsername, gameId = testService.authGameId)
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .header(testService.gameAuthHeaderName, testService.authGameId)
            .body(gameplayData)
            .post(gameplayEndpoint)
            .then().statusCode(HttpStatus.CREATED.value())
    }
}