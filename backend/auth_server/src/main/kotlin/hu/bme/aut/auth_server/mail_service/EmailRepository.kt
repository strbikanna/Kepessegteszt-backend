package hu.bme.aut.auth_server.mail_service

import org.springframework.data.repository.CrudRepository
import java.util.*

interface EmailRepository : CrudRepository<EmailVerificationEntity, Int> {
    fun findByVerificationKey(key: String): Optional<EmailVerificationEntity>
}