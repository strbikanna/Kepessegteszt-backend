package hu.bme.aut.auth_server.mail_service

import hu.bme.aut.auth_server.user.UserEntity
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entity for storing email verification keys.
 */
@Entity(name = "EMAIL")
data class EmailVerificationEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    var verificationKey: String,

    val valid: LocalDateTime,

    @OneToOne
    var user: UserEntity
)