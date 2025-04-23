package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.utils.POST_LOGOUT_REDIRECT_URI
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val loginEntryPoint = LoginUrlAuthenticationEntryPoint("/login")
    private val logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)

    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        val postLogoutRedirectUri = request?.getParameter(POST_LOGOUT_REDIRECT_URI)
        val idTokenHint = request?.getParameter("id_token_hint")
        if(postLogoutRedirectUri != null && idTokenHint != null) {
            logger.trace("Redirecting to postLogoutRedirectUri: $postLogoutRedirectUri")
            response?.sendRedirect(postLogoutRedirectUri)
            return
        }
        loginEntryPoint.commence(request, response, authException)
    }


}