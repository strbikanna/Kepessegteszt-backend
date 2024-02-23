package hu.bme.aut.auth_server.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class AuthServerConfig {

    @Value("\${cognitive-app.issuer-url}")
    private lateinit var issuerUrl: String

    /**
     * This filter chain has the settings of the oidc authorization server endpoints.
     * (e.g. /oauth2/authorize, /oauth2/token, /oauth2/userinfo, /.well-known/openid-configuration)
     */
    @Bean
    @Order(1)
    fun authServerSecurityFilterChain(http: HttpSecurity, userInfoMapper: UserInfoMapper): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)

        //openID 1.0 connection with custom userinfo endpoint
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc { oidc ->
                oidc.userInfoEndpoint { userinfo ->
                    userinfo.userInfoMapper { context -> userInfoMapper.mapUserInfo(context) }
                }
            }

        http
            .cors(withDefaults())
            .sessionManagement { SessionCreationPolicy.STATELESS }
            // Redirect to the login page when not authenticated from the authorization endpoint
            .exceptionHandling { exceptions ->
                exceptions
                    .defaultAuthenticationEntryPointFor(
                        LoginUrlAuthenticationEntryPoint("/login"),
                        MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
            } // Accept access tokens for User Management and/or Client Registration
            .oauth2ResourceServer { it.jwt(withDefaults()) }

        return http.build()
    }

    /**
     * The secondary filterchain, when the 1st cannot be applied.
     * (e.g. /login, /register, /mail)
     */
    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(withDefaults())
            .sessionManagement { SessionCreationPolicy.STATELESS }
            .authorizeHttpRequests {
                it.requestMatchers("/register").permitAll()
                it.requestMatchers("/mail/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
            // Redirect to the login page from the authorization server filter chain
            .formLogin {
                it.loginPage("/login").permitAll()
            }
        return http.build()
    }

    /**
     * This bean is responsible for converting the JWT token claims to a Spring Security Authentication object
     * with user roles.
     */
    @Bean
    fun customJwtAuthenticationConverter(converter: GrantedAuthoritiesCustomizer): JwtAuthenticationConverter {
        val authConverter = JwtAuthenticationConverter()
        authConverter.setJwtGrantedAuthoritiesConverter(converter)
        return authConverter
    }

    /**
     * This bean is responsible for loading the user details from the database.
     * It is used to determine granted roles and password.
     */
    @Bean
    fun userDetailsService(dataSource: DataSource): UserDetailsService {
        val userdetailsService = JdbcDaoImpl()
        userdetailsService.setDataSource(dataSource)
        userdetailsService.usersByUsernameQuery = ("select username,password,enabled "
                + "from users "
                + "where username = ?")
        userdetailsService.setAuthoritiesByUsernameQuery(
            "select users.username, roles.role_name "
                    + "from users "
                    + "inner join user_roles ur on ur.user_id = users.id "
                    + "inner join roles on ur.role_id = roles.role_name "
                    + "where users.username = ?"
        )
        return userdetailsService
    }

    /**
     * This bean is responsible for encoding and decoding the password.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * This bean is responsible for storing the registered clients.
     */
    @Bean
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate) = JdbcRegisteredClientRepository(jdbcTemplate)


    /**
     * This bean is responsible for storing the public key used for signing the JWT tokens.
     */
    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val jwkSet = JWKSet(generateRsaKey())
        return ImmutableJWKSet(jwkSet)
    }

    /**
     * This bean is responsible for decoding the JWT tokens.
     */
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    /**
     * This bean is responsible for the authorization server configuration settings.
     * Sets issuer uri which is used to sign the JWT tokens.
     */
    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer(issuerUrl).build()
    }

    /**
     * This bean is responsible for the CORS configuration.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("*")
        configuration.allowedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    private fun generateRsaKey(): RSAKey {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
    }
}