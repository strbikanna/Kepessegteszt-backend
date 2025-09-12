package hu.bme.aut.auth_server.error

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping



@Controller
class AppErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(): String {
        return "error"
    }
}