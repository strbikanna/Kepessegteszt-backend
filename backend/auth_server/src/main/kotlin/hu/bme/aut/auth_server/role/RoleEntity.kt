package hu.bme.aut.auth_server.role

import jakarta.persistence.*

enum class Role {
    ADMIN, PARENT, SCIENTIST, STUDENT, TEACHER,
    PARENT_REQUEST, TEACHER_REQUEST, SCIENTIST_REQUEST,
    GAME
}

@Entity(name = "ROLES")
data class RoleEntity(
    @Id
    @Enumerated(EnumType.STRING)
    val roleName: Role
) {
    fun isImpersonationRole(): Boolean = roleName == Role.PARENT || roleName == Role.TEACHER || roleName == Role.SCIENTIST || roleName == Role.ADMIN
}
