package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.utils.MOBILE_DEVICES
import hu.bme.aut.auth_server.utils.POST_LOGOUT_REDIRECT_URI
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nl.basjes.parse.useragent.UserAgent
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component

@Component
class LogoutErrorHandler(
    @Autowired private var useragentAnalyzer: UserAgentAnalyzer
) : AuthenticationFailureHandler {

    private val logger = LoggerFactory.getLogger(LogoutErrorHandler::class.java)

    @Value("\${server.servlet.context-path}")
    private var contextPath: String = ""


    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        logger.error("Logout error: ${exception?.message}")

        val postLogoutRedirectUri = request?.getParameter(POST_LOGOUT_REDIRECT_URI)
        val idTokenHint = request?.getParameter("id_token_hint")
        val userAgentHeader = request?.getHeader(HttpHeaders.USER_AGENT) ?: ""
        val userAgent = useragentAnalyzer.parse(userAgentHeader)
        val deviceClass = userAgent.getValue(UserAgent.DEVICE_CLASS)
        val isMobile = deviceClass in MOBILE_DEVICES

        if(postLogoutRedirectUri != null && idTokenHint != null) {
            logger.trace("Redirecting to postLogoutRedirectUri: $postLogoutRedirectUri")
            if(isMobile) {
                response?.sendRedirect("$contextPath/mobile-logout?$POST_LOGOUT_REDIRECT_URI=$postLogoutRedirectUri")
            }else{
                response?.sendRedirect(postLogoutRedirectUri)
            }
        }


    }
}