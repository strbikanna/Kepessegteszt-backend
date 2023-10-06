package hu.bme.aut.resource_server.user.role

import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @Enumerated(value= EnumType.STRING)
    @Column(name="_name")
    val roleName: RoleName,
)

enum class RoleName{
    ADMIN, TEACHER, PARENT, STUDENT, SCIENTIST
}

