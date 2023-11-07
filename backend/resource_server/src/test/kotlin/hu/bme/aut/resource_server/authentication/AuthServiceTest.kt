package hu.bme.aut.resource_server.authentication

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest(
    @Autowired private var authService: AuthService
) {

    @Test
    fun shouldNotThrowWhenContactExists(){
        val testUsername= "test_user"
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
        Mockito.`when`(mockRequestHeadersSpec.header("Authorization", "Bearer $testToken")).thenReturn(mockRequestHeadersSpec)
        Mockito.`when`(mockRequestHeadersSpec.retrieve()).thenReturn(mockResponseSpec)
        Mockito.`when`(mockResponseSpec.bodyToMono(String::class.java)).thenReturn(Mono.just(response))
        assertDoesNotThrow { authService.checkContactAndThrow(mockAuthentication, testUsername)}
    }
}