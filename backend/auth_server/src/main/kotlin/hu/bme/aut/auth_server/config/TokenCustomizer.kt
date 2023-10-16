package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.user.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.MissingRequiredPropertiesException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Service

@Service
class TokenCustomizer(
    @Autowired private var userInfoService: UserInfoService,
) : OAuth2TokenCustomizer<JwtEncodingContext> {

    /**
     * Customizes access token as following:
     * If additional parameter "act_as" is provided, then impersonates the requested user if allowed.
     * If scope is "game" and additional parameter "game_id" is provided, then creates game access token
     * which contains user as subject with exclusively GAME role and game_id as claim.
     */
    override fun customize(context: JwtEncodingContext) {
        if(context.tokenType != OAuth2TokenType.ACCESS_TOKEN) return

        var username = context.getPrincipal<Authentication>().name
        username = checkActAsRequestedUsername(context, username)
        val scope = context.authorizedScopes

        val userInfo = userInfoService.loadUserInfoByUsername(username)
        if(scope.contains("game")){
            context.claims.claims { claims ->
                claims["roles"] = listOf(Role.GAME)
                claims["game_id"] = getGameId(context)
            }
        }else{
            context.claims
                .claims { claims ->
                    claims.putAll(userInfo.claims)
                }
        }
    }

    private fun checkActAsRequestedUsername(context: JwtEncodingContext, username: String): String {
        if (!userInfoService.hasImpersonationRole(username)) return username
        var originalUsername = username
        val authRequest =
            context.authorization?.attributes?.get("org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest") as OAuth2AuthorizationRequest?
                ?: return username

        if (authRequest.additionalParameters.containsKey("act_as")) {
            val requestedUsername = getRequestedUsername(authRequest.additionalParameters)
            requestedUsername?.let {
                if (userInfoService.existsContact(originalUsername, it)) originalUsername = requestedUsername
            }
        }
        return originalUsername
    }

    private fun getRequestedUsername(map: Map<String, Any>): String {
        val requestedUsername = map["act_as"] as String
        if (userInfoService.existsByUsername(requestedUsername)) return requestedUsername
        throw UsernameNotFoundException("No user found for mimic request \"$requestedUsername\".")
    }

    private fun getGameId(context: JwtEncodingContext): String{
        val authRequest =
            context.authorization?.attributes?.get("org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest") as OAuth2AuthorizationRequest?
                ?: throw MissingRequiredPropertiesException()
        if (authRequest.additionalParameters.containsKey("game_id")) {
            return authRequest.additionalParameters["game_id"] as String
        }
        throw MissingRequiredPropertiesException()
    }
}