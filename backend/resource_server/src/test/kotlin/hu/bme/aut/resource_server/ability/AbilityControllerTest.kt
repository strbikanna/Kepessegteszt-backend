package hu.bme.aut.resource_server.ability

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.utils.RoleName
import io.restassured.RestAssured.given
import io.restassured.matcher.RestAssuredMatchers.*
import org.hamcrest.Matchers.*
import io.restassured.builder.RequestSpecBuilder
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AbilityControllerTest(
    @Autowired private var testService: TestUtilsService
) {
    @LocalServerPort
    private var port: Int? = null

    private lateinit var requestSpec: RequestSpecification

    private val abilityEndpoint = "/ability"

    @BeforeEach
    fun init() {
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
    fun shouldGetAllAbilities() {
        testService.saveAuthUserWithRights(RoleName.ADMIN)
        val response = given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .get("${abilityEndpoint}/all")
            .then().statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("size()", equalTo(5))
    }

    @Test
    fun shouldAddAbility(){
        testService.saveAuthUserWithRights(RoleName.STUDENT, RoleName.ADMIN)
        val abilityEntity = AbilityEntity("Gt", "testAbility", "testDescription")
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(abilityEntity)
            .post(abilityEndpoint)
            .then().statusCode(HttpStatus.CREATED.value())
            .assertThat()
            .body("code", equalTo(abilityEntity.code))
            .body("name", equalTo(abilityEntity.name))
            .body("description", equalTo(abilityEntity.description))
        assertEquals(6, testService.abilityRepository.count())
    }

    @Test
    fun shouldUpdateAbility(){
        testService.saveAuthUserWithRights(RoleName.STUDENT, RoleName.ADMIN)
        val abilityEntity = AbilityEntity("Gf", "updated name", "testDescription")
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(abilityEntity)
            .put("${abilityEndpoint}/${abilityEntity.code}")
            .then().statusCode(HttpStatus.OK.value())
            .assertThat()
            .body("code", equalTo(abilityEntity.code))
            .body("name", equalTo(abilityEntity.name))
            .body("description", equalTo(abilityEntity.description))
        assertEquals(5, testService.abilityRepository.count())
    }

    @Test
    fun shouldNotAllowRequestWithNoSufficientRole(){
        testService.saveAuthUserWithRights(RoleName.STUDENT)
        //get all abilities
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .get("${abilityEndpoint}/all")
            .then().statusCode(HttpStatus.FORBIDDEN.value())

        val abilityEntity = AbilityEntity("Gt", "test ability name", "testDescription")
        //save new ability
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(abilityEntity)
            .post(abilityEndpoint)
            .then().statusCode(HttpStatus.FORBIDDEN.value())
        //update ability
        given(requestSpec)
            .header(testService.authHeaderName, testService.authUsername)
            .body(abilityEntity)
            .put("${abilityEndpoint}/${abilityEntity.code}")
            .then().statusCode(HttpStatus.FORBIDDEN.value())

    }
}