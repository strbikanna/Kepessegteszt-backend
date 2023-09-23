package hu.bme.aut.auth_server.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
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
import org.springframework.security.crypto.password.NoOpPasswordEncoder
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
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class AuthServerConfig {

    @Bean
    @Order(1)
    fun authServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)

        //openID 1.0 connection
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java).oidc(withDefaults())

        http
            .sessionManagement { SessionCreationPolicy.STATELESS }
            // Redirect to the login page when not authenticated from the authorization endpoint
            .exceptionHandling { exceptions ->
                exceptions
                    .defaultAuthenticationEntryPointFor(
                        //TODO customize login page
                        LoginUrlAuthenticationEntryPoint("/login"),
                        MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                    )
            } // Accept access tokens for User Info and/or Client Registration
            .oauth2ResourceServer { it.jwt(withDefaults()) }

        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement { SessionCreationPolicy.STATELESS }
            .authorizeHttpRequests {
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
            // Redirect to the login page from the authorization server filter chain
            .formLogin(withDefaults())
        return http.build()
    }

    @Bean
    fun customJwtAuthenticationConverter(converter: GrantedAuthoritiesCustomizer): JwtAuthenticationConverter {
        val authConverter = JwtAuthenticationConverter()
        authConverter.setJwtGrantedAuthoritiesConverter(converter)
        return authConverter
    }

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
                    + "inner join roles on ur.role_id = roles.id "
                    + "where users.username = ?"
        )
        return userdetailsService
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    fun registeredClientRepository(jdbcTemplate: JdbcTemplate) = JdbcRegisteredClientRepository(jdbcTemplate)


    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val jwkSet = JWKSet(generateRsaKey())
        return ImmutableJWKSet(jwkSet)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().issuer("http://localhost:9000").build()
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