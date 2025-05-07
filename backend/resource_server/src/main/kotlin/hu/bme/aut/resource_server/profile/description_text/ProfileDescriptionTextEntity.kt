package hu.bme.aut.resource_server.profile.description_text

import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity(name= "profile_description")
data class ProfileDescriptionTextEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "generated_text")
    val generatedText: String,

    @CreationTimestamp
    var timestamp: LocalDateTime? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val user: UserEntity,
)
