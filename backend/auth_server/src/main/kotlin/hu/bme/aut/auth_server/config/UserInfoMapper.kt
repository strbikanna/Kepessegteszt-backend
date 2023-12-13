package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.user.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken
import org.springframework.stereotype.Service

/**
 * Returns user data from database for the UserInfo endpoint.
 */
@Service
class UserInfoMapper(@Autowired private var userInfoService: UserInfoService) {
    fun mapUserInfo(authContext: OidcUserInfoAuthenticationContext): OidcUserInfo {
        val username = authContext.getAuthentication<OidcUserInfoAuthenticationToken>().name
        return userInfoService.loadUserInfoByUsername(username)
    }
}