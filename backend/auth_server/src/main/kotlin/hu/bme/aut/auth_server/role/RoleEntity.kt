package hu.bme.aut.auth_server.role

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id

enum class Role {
    ADMIN, PARENT, SCIENTIST, STUDENT, TEACHER
}

@Entity(name = "ROLES")
data class RoleEntity(
    @Id
    val id: Int,

    @Enumerated(EnumType.STRING)
    val roleName: Role
) {
    fun isMimicRole(): Boolean = roleName == Role.PARENT || roleName == Role.TEACHER || roleName == Role.SCIENTIST
}
