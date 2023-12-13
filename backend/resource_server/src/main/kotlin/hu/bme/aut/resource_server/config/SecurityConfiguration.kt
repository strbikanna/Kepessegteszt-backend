package hu.bme.aut.resource_server.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Security configuration for the resource server.
 * Uses oauth2 jwt authentication.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile( "!test" )
@ConditionalOnProperty(name = ["cognitive-app.resource-server.security.bypass"], havingValue = "false", matchIfMissing = true)
class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        http
            .csrf{ it.disable()}
            .cors(withDefaults())
            .sessionManagement { SessionCreationPolicy.STATELESS }
            .authorizeHttpRequests{
                it.requestMatchers(HttpMethod.OPTIONS).permitAll()
                it.requestMatchers("/error").permitAll()
                it.requestMatchers("/api-docs").permitAll()
                it.requestMatchers("/swagger-ui/**").permitAll()
                it.requestMatchers("/api-docs/swagger-config").permitAll()
                it.requestMatchers("/game_images/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
        return http.build()
    }

    /**
     * CORS settings allowing all origins, methods and headers.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = mutableListOf("*")
        configuration.allowedMethods = mutableListOf("*")
        configuration.allowedHeaders = mutableListOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        //headers authorization content type
        return source
    }

    /**
     * Custom converter for jwt authentication.
     */
    @Bean
    fun customJwtAuthenticationConverter(converter: GrantedAuthoritiesConverter): JwtAuthenticationConverter {
        val authConverter = JwtAuthenticationConverter()
        authConverter.setJwtGrantedAuthoritiesConverter(converter)
        return authConverter
    }
}
