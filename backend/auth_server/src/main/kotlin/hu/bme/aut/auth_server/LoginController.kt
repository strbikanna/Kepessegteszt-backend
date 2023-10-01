package hu.bme.aut.auth_server

import hu.bme.aut.auth_server.user.UserRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import java.sql.SQLException

@Controller
class LoginController(
    @Autowired private var userService: UserRegistrationService
) {
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
        try {
            userService.saveUserOrThrowException(user)
        } catch (exception: SQLException) {
            return "redirect:register/?error"
        }
        return "register-success"
    }
}

class RegistrationData(

) {
    var email: String = ""

    var firstName: String = ""

    var lastName: String = ""

    var username: String = ""

    var role: String = ""

    var password: String = ""
}