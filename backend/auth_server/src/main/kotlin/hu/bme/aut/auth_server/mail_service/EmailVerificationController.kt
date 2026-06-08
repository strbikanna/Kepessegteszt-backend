package hu.bme.aut.auth_server.mail_service

import hu.bme.aut.auth_server.ForgotPasswordData
import hu.bme.aut.auth_server.user.UserManagementService
import hu.bme.aut.auth_server.user.UserRegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/mail")
class EmailVerificationController(
    @Autowired private var emailVerificationService: EmailVerificationService,
    @Autowired private var userService: UserManagementService,
    @Autowired private var userRegistrationService: UserRegistrationService
) {
    @GetMapping("/verification")
    fun verifyEmail(@RequestParam verificationKey: String, @RequestParam username: String): String {
        val verificationSuccess = emailVerificationService.verifyEmail(verificationKey, username)
        if (verificationSuccess) {
            emailVerificationService.removeVerificationEntity(verificationKey)
            val user = userService.loadUserByUsername(username)
            user.get().enabled = true
            userService.save(user.get())
            return "register-success"
        } else
            return "email-verification-success/?error"
    }

    @GetMapping("/reset-password")
    fun resetPassword(
        @RequestParam verificationKey: String,
        @RequestParam username: String,
        model: Model
    ): String {
        val verificationSuccess = emailVerificationService.verifyEmail(verificationKey, username)
        if (verificationSuccess) {
            model.addAttribute("passwordData", ForgotPasswordData(verificationCode = verificationKey, username = username))
            return "forgot-password-reset"
        } else
            return "email-verification-success/?error"
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @ModelAttribute("passwordData") data: ForgotPasswordData,
        model: Model
    ): String {
        val verificationSuccess = emailVerificationService.verifyEmail(data.verificationCode, data.username)
        if (verificationSuccess) {
            emailVerificationService.removeVerificationEntity(data.verificationCode)
            userRegistrationService.changePassword(data.username, data.newPassword)
            return "forgot-password-success"
        } else
            return "forgot-password-success/?error"
    }
}