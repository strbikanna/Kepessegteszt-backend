package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.utils.MOBILE_DEVICES
import hu.bme.aut.auth_server.utils.POST_LOGOUT_REDIRECT_URI
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import nl.basjes.parse.useragent.UserAgent
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service

@Service
class LogoutSuccessHandler (
    @Autowired private var useragentAnalyzer: UserAgentAnalyzer
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        // Invalidate session
        val session = request?.session
        session?.invalidate()

        // Clear authentication
        val securityContext = SecurityContextHolder.getContext()
        securityContext.authentication = null
        SecurityContextHolder.clearContext()


        val redirectUri = request?.getParameter(POST_LOGOUT_REDIRECT_URI)
        val userAgentHeader = request?.getHeader(HttpHeaders.USER_AGENT) ?: ""
        val userAgent = useragentAnalyzer.parse(userAgentHeader)
        val deviceClass = userAgent.getValue(UserAgent.DEVICE_CLASS)
        val isMobile = deviceClass in MOBILE_DEVICES

        if (isMobile) {
            // Redirect mobile users to a custom logout page with redirect uri param
            response?.sendRedirect("/mobile-logout?$POST_LOGOUT_REDIRECT_URI=$redirectUri")
        } else {
            // Redirect web users back to the app
            response?.sendRedirect(redirectUri)
        }
    }
}