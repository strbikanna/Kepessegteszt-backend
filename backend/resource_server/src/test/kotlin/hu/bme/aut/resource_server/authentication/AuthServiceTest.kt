package hu.bme.aut.resource_server.authentication

import hu.bme.aut.resource_server.error.ApiCallException
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user_group.UserGroup
import hu.bme.aut.resource_server.utils.RoleName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.Optional


class AuthServiceTest {

    private lateinit var authService: AuthService

    private lateinit var mockRecommendedGameRepository: hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
    private lateinit var mockUserRepository: hu.bme.aut.resource_server.user.UserRepository
    private lateinit var mockUserGroupRepository: hu.bme.aut.resource_server.user_group.UserGroupRepository

    @BeforeEach
    fun setup() {
        mockRecommendedGameRepository = mock(hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository::class.java)
        mockUserRepository = mock(hu.bme.aut.resource_server.user.UserRepository::class.java)
        mockUserGroupRepository = mock(hu.bme.aut.resource_server.user_group.UserGroupRepository::class.java)

        authService = AuthService(
            mockRecommendedGameRepository,
            mockUserRepository,
            mockUserGroupRepository
        )
    }

    @Test
    fun shouldReturnTrueIfContactExists() {
        val testUsername = "test_user"
        val testToken = "mockToken"
        val response = "{\"contacts\": [\"$testUsername\"]}"

        val mockAuthentication = mock(Authentication::class.java)
        val mockJwt = mock(Jwt::class.java)
        Mockito.`when`(mockAuthentication.principal).thenReturn(mockJwt)
        Mockito.`when`(mockJwt.tokenValue).thenReturn(testToken)

        val mockWebClient = mock(WebClient::class.java)
        val mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
        val mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
        val mockResponseSpec = mock(WebClient.ResponseSpec::class.java)
        authService.webclient = mockWebClient
        Mockito.`when`(mockWebClient.get()).thenReturn(mockRequestHeadersUriSpec)
        Mockito.`when`(mockRequestHeadersUriSpec.uri("/user/impersonation_contacts")).thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.header("Authorization", "Bearer $testToken"))
            .thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        Mockito.`when`(mockResponseSpec.bodyToMono(String::class.java)).thenReturn(Mono.just(response))
        runBlocking {
            val isContact = authService.isContact(mockAuthentication, testUsername)
            assertTrue(isContact)
        }
    }

    @Test
    fun shouldThrowWhenErrorToReachAuthServer() {
        val testToken = "mockToken"

        val mockAuthentication = mock(Authentication::class.java)
        val mockJwt = mock(Jwt::class.java)
        Mockito.`when`(mockAuthentication.principal).thenReturn(mockJwt)
        Mockito.`when`(mockJwt.tokenValue).thenReturn(testToken)

        val mockWebClient = mock(WebClient::class.java)
        val mockRequestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec::class.java)
        val mockRequestHeadersSpec = mock(WebClient.RequestHeadersSpec::class.java)
        val mockResponseSpec = mock(WebClient.ResponseSpec::class.java)
        authService.webclient = mockWebClient
        Mockito.`when`(mockWebClient.delete()).thenReturn(mockRequestHeadersUriSpec)
        Mockito.`when`(mockRequestHeadersUriSpec.uri("/user/me")).thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.accept(MediaType.APPLICATION_JSON)).thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.header("Authorization", "Bearer $testToken"))
            .thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        Mockito.`when`(mockResponseSpec.toBodilessEntity())
            .thenReturn(Mono.just(ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR)))

        assertThrows<ApiCallException> { runBlocking { authService.removeUserFromAuthServer(mockAuthentication) } }

    }

    @Test
    fun checkUserGroupWriteAndThrow_allowsAdminUser() {
        val username = "admin_user"
        val user = UserEntity(
            id = 1,
            firstName = "A",
            lastName = "B",
            username = username,
            roles = mutableSetOf(Role(RoleName.ADMIN))
        )

        Mockito.`when`(mockUserRepository.findByUsernameWithRoles(username)).thenReturn(Optional.of(user))
        val mockAuth = mock(Authentication::class.java)
        Mockito.`when`(mockAuth.name).thenReturn(username)

        // Should not throw for admin
        authService.checkUserGroupWriteAndThrow(mockAuth, 999)
    }

    @Test
    fun checkGroupDataReadAndThrow_throwsWhenNotMemberOrAdmin() {
        val username = "plain_user"
        val user = UserEntity(
            id = 2,
            firstName = "C",
            lastName = "D",
            username = username,
            roles = mutableSetOf()
        )

        Mockito.`when`(mockUserRepository.findByUsernameWithRoles(username)).thenReturn(Optional.of(user))
        val mockAuth = mock(Authentication::class.java)
        Mockito.`when`(mockAuth.name).thenReturn(username)

        val mockGroup = mock(UserGroup::class.java)
        Mockito.`when`(mockUserGroupRepository.findById(10)).thenReturn(Optional.of(mockGroup))
        Mockito.`when`(mockGroup.members).thenReturn(mutableSetOf())
        Mockito.`when`(mockGroup.admins).thenReturn(mutableSetOf())
        Mockito.`when`(mockGroup.getAllUserIds()).thenReturn(setOf())

        assertThrows<IllegalAccessException> { authService.checkGroupDataReadAndThrow(mockAuth, 10) }
    }
}