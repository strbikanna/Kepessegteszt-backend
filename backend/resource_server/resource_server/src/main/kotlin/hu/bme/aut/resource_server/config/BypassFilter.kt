package hu.bme.aut.resource_server.config

import hu.bme.aut.resource_server.game.GameRepository
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
    @Autowired private var userRepository: UserRepository,
    @Autowired private var gameRepository: GameRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestedUsername = request.getHeader("authUser")
        val requestedGameId = request.getHeader("authGame")
        if(requestedUsername == null || requestedUsername.length > 50){
            filterChain.doFilter(request, response)
            return
        }
        if(requestedGameId != null){
            authenticateGame(requestedGameId, requestedUsername)
        }else{
            authenticateUser(requestedUsername)
        }

        filterChain.doFilter(request, response)
    }

    private fun authenticateGame(requestedGameId: String, requestedUsername: String) {
        val gameOptional = gameRepository.findById(Integer.parseInt(requestedGameId))
        if(gameOptional.isEmpty){
            return
        }
        val game = gameOptional.get()
        val jwt = Jwt.withTokenValue("devgame")
            .header("alg", "none")
            .subject(requestedGameId)
            .build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, listOf(SimpleGrantedAuthority("ROLE_GAME"), SimpleGrantedAuthority(requestedUsername)))
    }

    private fun authenticateUser(requestedUsername: String) {
        val userOptional = userRepository.findByUsernameWithRoles(requestedUsername)
        val user: UserEntity
        if(userOptional.isPresent){
            user = userOptional.get()
        }else{
            return
        }
        val jwt = Jwt.withTokenValue("devuser")
            .header("alg", "none")
            .subject(requestedUsername)
            .build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, user.roles.map{SimpleGrantedAuthority("ROLE_${it.roleName.toString()}")})
    }
}