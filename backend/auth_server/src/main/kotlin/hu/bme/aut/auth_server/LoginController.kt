package hu.bme.aut.auth_server

import hu.bme.aut.auth_server.mail_service.EmailService
import hu.bme.aut.auth_server.mail_service.EmailVerificationService
import hu.bme.aut.auth_server.user.UserEntity
import hu.bme.aut.auth_server.user.UserRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.sql.SQLIntegrityConstraintViolationException

@Controller
class LoginController(
    @Autowired private var userService: UserRegistrationService,
    @Autowired private var emailVerificationService: EmailVerificationService,
    @Autowired private var emailSenderService: EmailService
) {
    private val badRegistrationCache: MutableList<RegistrationData> = mutableListOf()

    @GetMapping("/login")
    fun loginPage(): String {
        return "login"
    }

    @GetMapping("/register")
    fun registerPage(model: Model): String {
        model.addAttribute("user", RegistrationData())
        return "register"
    }

    @PostMapping("/register")
    fun registerUser(user: RegistrationData): String {
        badRegistrationCache.add(user)
        val userEntity = userService.saveUserOrThrowException(user)
        sendVerificationMail(userEntity)
        badRegistrationCache.remove(user)
        return "register-success"
    }

    private fun sendVerificationMail(userEntity: UserEntity) {
        val verification = emailVerificationService.createVerificationEntity(userEntity)
        val message = emailVerificationService.createVerificationMessage(verification)
        emailSenderService.sendSimpleEmail(to = userEntity.email, text = message)
    }

    @ExceptionHandler
    fun handleDuplicateUsername(ex: SQLIntegrityConstraintViolationException, model: Model): String {
        val possibleUsers = badRegistrationCache.filter { ex.message?.contains(it.username) ?: false }
        if (possibleUsers.size == 1) {
            val user = possibleUsers[0]
            user.error = true
            model.addAttribute("user", user)
        } else {
            model.addAttribute("user", RegistrationData(error = true))
        }
        return "register"
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