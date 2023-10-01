package hu.bme.aut.auth_server.mail_service

import hu.bme.aut.auth_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

@Service
class EmailVerificationService(
    @Autowired private var emailRepository: EmailRepository
) {
    fun createVerificationMessage(verificationEntity: EmailVerificationEntity): String {
        val user = verificationEntity.user
        val url = UriComponentsBuilder
            .fromHttpUrl("http://localhost:9000/mail/verification")
            .queryParam("verificationKey", verificationEntity.verificationKey)
            .queryParam("username", user.username)
            .build()
            .toUriString()
        val message = "Kedves ${user.lastName}!\nAz alábbi linkre kattintva megerősítheted az email címed: " +
                "Ezzel véglegesítheted a regisztrációd.\n" +
                "${url}\nÜdv,\nCognitive App Csapat"
        return message

    }

    fun verifyEmail(verificationKey: String, username: String): Boolean {
        val verification = emailRepository.findByVerificationKey(verificationKey)
        if (verification.isEmpty) {
            return false
        }
        val user = verification.get().user
        return user.username == username && verification.get().valid.isBefore(LocalDateTime.now())
    }

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