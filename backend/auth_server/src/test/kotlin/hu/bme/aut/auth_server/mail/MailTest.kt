package hu.bme.aut.auth_server.mail

import hu.bme.aut.auth_server.mail_service.EmailService
import hu.bme.aut.auth_server.mail_service.EmailVerificationService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class MailTest(
    @Autowired private var mailService: EmailService,
    @Autowired private var mailVerificationService: EmailVerificationService
) {
    @Test
    fun shouldSendEmail() {
        mailService.sendSimpleEmail(
            to = "strbikan@gmail.com",
            text = "Helloka! Ez egy teszt email a mail service-emből.\n" +
                    "\nBy :)"
        )
    }
}