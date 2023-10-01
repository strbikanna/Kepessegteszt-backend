package hu.bme.aut.auth_server

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
    @Autowired private var userService: UserRegistrationService
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
        userService.saveUserOrThrowException(user)
        badRegistrationCache.remove(user)
        return "register-success"
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