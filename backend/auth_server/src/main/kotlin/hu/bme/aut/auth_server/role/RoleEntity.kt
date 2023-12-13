package hu.bme.aut.auth_server.role

import jakarta.persistence.*

/**
 * Role enum class
 * used for defining the roles of the users
 */
enum class Role {
    ADMIN, PARENT, SCIENTIST, STUDENT, TEACHER,
    PARENT_REQUEST, TEACHER_REQUEST, SCIENTIST_REQUEST,
    GAME
}

/**
 * Entity class representing role based authorities.
 */
@Entity(name = "ROLES")
data class RoleEntity(
    @Id
    @Enumerated(EnumType.STRING)
    val roleName: Role
) {
    /**
     * Returns true if the role allowed to impersonate.
     */
    fun isImpersonationRole(): Boolean = roleName == Role.PARENT || roleName == Role.TEACHER || roleName == Role.SCIENTIST || roleName == Role.ADMIN
}
