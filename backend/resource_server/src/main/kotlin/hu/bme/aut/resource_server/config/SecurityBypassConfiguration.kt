package hu.bme.aut.resource_server.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.ConcurrentSessionFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile( "test")
@ConditionalOnProperty(name = ["cognitive-app.resource-server.security.bypass"], havingValue = "true", matchIfMissing = false)
class SecurityBypassConfiguration {
    @Bean
    fun filterChain(http: HttpSecurity, bypassFilter: BypassFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { SessionCreationPolicy.STATELESS }
            .authorizeHttpRequests {
                it.requestMatchers("/error").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterAfter(bypassFilter, ConcurrentSessionFilter::class.java)
        return http.build()
    }
}