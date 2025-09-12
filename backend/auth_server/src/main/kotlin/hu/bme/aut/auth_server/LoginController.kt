package hu.bme.aut.auth_server

import hu.bme.aut.auth_server.mail_service.EmailService
import hu.bme.aut.auth_server.mail_service.EmailVerificationService
import hu.bme.aut.auth_server.user.UserEntity
import hu.bme.aut.auth_server.user.UserRegistrationService
import hu.bme.aut.auth_server.utils.MOBILE_DEVICES
import hu.bme.aut.auth_server.utils.POST_LOGOUT_REDIRECT_URI
import nl.basjes.parse.useragent.UserAgent
import nl.basjes.parse.useragent.UserAgentAnalyzer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.sql.SQLIntegrityConstraintViolationException

@Controller
class LoginController(
    @Autowired private var userService: UserRegistrationService,
    @Autowired private var emailVerificationService: EmailVerificationService,
    @Autowired private var emailSenderService: EmailService,
    @Autowired private var useragentAnalyzer: UserAgentAnalyzer
) {

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/mobile-logout")
    fun logout(
        @RequestParam(POST_LOGOUT_REDIRECT_URI) redirectUri: String,
        model: Model
    ): String{
        model.addAttribute(POST_LOGOUT_REDIRECT_URI, redirectUri)
        return "mobile-logout"
    }

    @GetMapping("/register")
    fun registerPage(
        @RequestHeader(HttpHeaders.USER_AGENT) userAgentString: String,
        model: Model
    ): String {
        model.addAttribute("user", RegistrationData())
        return registerPageContent(userAgentString, model)
    }

    private fun registerPageContent(userAgentString: String, model: Model): String {
        val userAgent = useragentAnalyzer.parse(userAgentString)
        val deviceClass = userAgent.getValue(UserAgent.DEVICE_CLASS)

        if (deviceClass in MOBILE_DEVICES) {
            return "register-mobile"
        }
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(
        user: RegistrationData,
        model: Model,
        @RequestHeader(HttpHeaders.USER_AGENT) userAgentString: String
    ): String {
        val userEntity = try{
            userService.saveUserOrThrowException(user)
        } catch(ex: IllegalArgumentException) {
            return handleDuplicateUsernameError(user, model, userAgentString)
        }
        sendVerificationMail(userEntity)
        return "email-verification-sent"
    }

    private fun sendVerificationMail(userEntity: UserEntity) {
        val verification = emailVerificationService.createVerificationEntity(userEntity)
        val message = emailVerificationService.createVerificationMessage(verification)
        emailSenderService.sendSimpleEmail(to = userEntity.email, text = message)
    }


    private fun handleDuplicateUsernameError(user: RegistrationData, model: Model, userAgentString: String): String {
        user.error = true
        model.addAttribute("user", user)
        return registerPageContent(userAgentString, model)
    }

}

class RegistrationData(
    error: Boolean = false
) {
    var email: String = ""

    var firstName: String = ""

    var lastName: String = ""

    var username: String = ""

    var role: String = ""

    var password: String = ""

    var error = error
}