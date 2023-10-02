package hu.bme.aut.resource_server.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    val roleName: RoleName
)

enum class RoleName{
    ADMIN, TEACHER, PARENT, STUDENT, SCIENTIST
}

