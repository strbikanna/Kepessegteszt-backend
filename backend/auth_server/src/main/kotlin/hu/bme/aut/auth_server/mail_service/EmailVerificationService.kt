package hu.bme.aut.auth_server.mail_service

import hu.bme.aut.auth_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

/**
 * Service for email verification.
 */
@Service
class EmailVerificationService(
    @Autowired private var emailRepository: EmailRepository
) {
    @Value("\${cognitive-app.issuer-url}")
    private lateinit var appBaseUrl: String

    /**
     * Creates the verification email text and url for the user.
     */
    fun createVerificationMessage(verificationEntity: EmailVerificationEntity): String {
        val user = verificationEntity.user
        val url = UriComponentsBuilder
            .fromHttpUrl("$appBaseUrl/mail/verification")
            .queryParam("verificationKey", verificationEntity.verificationKey)
            .queryParam("username", user.username)
            .build()
            .toUriString()
        val message = "Kedves ${user.lastName}!\nSikeresen regisztráltál. Az alábbi linkre kattintva megerősítheted az email címed. " +
                "Ezzel véglegesíted a regisztrációd. A link 30 percig érvényes.\n" +
                "${url}\nÜdv,\nCognitive App Csapat"
        return message
    }

    /**
     * Verifies the email address of the user
     * based on the verification key and username.
     */
    fun verifyEmail(verificationKey: String, username: String): Boolean {
        val verification = emailRepository.findByVerificationKey(verificationKey)
        if (verification.isEmpty) {
            return false
        }
        val user = verification.get().user
        return user.username == username && verification.get().valid.isAfter(LocalDateTime.now())
    }

    /**
     * Creates a verification entity for the user
     * which stores the verification key and the expiration date.
     */
    fun createVerificationEntity(user: UserEntity): EmailVerificationEntity {
        val verification = EmailVerificationEntity(
            verificationKey = UUID.randomUUID().toString(),
            valid = LocalDateTime.now().plusMinutes(30),
            user = user
        )
        emailRepository.save(verification)
        return verification
    }
}