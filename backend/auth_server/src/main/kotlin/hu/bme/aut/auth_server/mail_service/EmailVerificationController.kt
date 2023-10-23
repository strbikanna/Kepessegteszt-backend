package hu.bme.aut.auth_server.mail_service

import hu.bme.aut.auth_server.user.UserManagementService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mail")
class EmailVerificationController(
    @Autowired private var emailVerificationService: EmailVerificationService,
    @Autowired private var userService: UserManagementService
) {
    @GetMapping("/verification")
    fun verifyEmail(@RequestParam verificationKey: String, @RequestParam username: String): String {
        val verificationSuccess = emailVerificationService.verifyEmail(verificationKey, username)
        if (verificationSuccess) {
            val user = userService.loadUserByUsername(username)
            user.get().enabled = true
            userService.save(user.get())
            return "email-verification-success"
        } else
            return "email-verification-success/?error"
    }
}