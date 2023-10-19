package hu.bme.aut.resource_server.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile( "!test & !dev" )
@ConditionalOnProperty(name = ["cognitive-app.resource-server.security.bypass"], havingValue = "false", matchIfMissing = false)
class SecurityConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain{
        http
            .csrf{ it.disable()}
            .cors(withDefaults())
            .sessionManagement { SessionCreationPolicy.STATELESS }
            .authorizeHttpRequests{
                it.requestMatchers("/error").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt(withDefaults()) }
        return http.build()
    }
}