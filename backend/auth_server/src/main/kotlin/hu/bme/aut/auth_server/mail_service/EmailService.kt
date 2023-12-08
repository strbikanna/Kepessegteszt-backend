package hu.bme.aut.auth_server.mail_service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * Service for sending emails.
 */
@Service
class EmailService(@Autowired private var emailSender: JavaMailSender) {
    @Value("\${cognitive-app.mail}")
    private lateinit var appEmailAddress: String

    @Value("\${cognitive-app.subject}")
    private lateinit var defaultSubject: String
    fun sendSimpleEmail(to: String, subject: String = defaultSubject, text: String) {
        val message = SimpleMailMessage()
        message.from = appEmailAddress
        message.setTo(to)
        message.subject = subject
        message.text = text
        emailSender.send(message)
    }
}