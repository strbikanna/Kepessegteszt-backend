package hu.bme.aut.auth_server.role

import jakarta.persistence.*

enum class Role {
    ADMIN, PARENT, SCIENTIST, STUDENT, TEACHER,
    PARENT_REQUEST, TEACHER_REQUEST, SCIENTIST_REQUEST
}

@Entity(name = "ROLES")
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @Enumerated(EnumType.STRING)
    val roleName: Role
) {
    fun isMimicRole(): Boolean = roleName == Role.PARENT || roleName == Role.TEACHER || roleName == Role.SCIENTIST
}
