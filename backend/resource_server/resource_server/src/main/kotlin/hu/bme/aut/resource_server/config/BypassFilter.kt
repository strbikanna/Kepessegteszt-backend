package hu.bme.aut.resource_server.config

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Profile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Profile( "test", "dev" )
@ConditionalOnProperty(name = ["cognitive-app.resource-server.security.bypass"], havingValue = "true", matchIfMissing = true)
class BypassFilter(
    @Autowired private var userRepository: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestedUsername = request.getHeader("authUser")
        if(requestedUsername == null || requestedUsername.length > 50){
            filterChain.doFilter(request, response)
            return
        }
        val userOptional = userRepository.findByUsernameWithRoles(requestedUsername)
        val user: UserEntity
        if(userOptional.isPresent){
            user = userOptional.get()
        }else{
            filterChain.doFilter(request, response)
            return
        }
        val jwt = Jwt.withTokenValue("devuser")
            .header("alg", "none")
            .subject(requestedUsername)
            .build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, user.roles.map{SimpleGrantedAuthority("ROLE_${it.roleName.toString()}")})
        filterChain.doFilter(request, response)
    }
}