package hu.bme.aut.resource_server.role

import hu.bme.aut.resource_server.utils.RoleName
import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @Enumerated(value= EnumType.STRING)
    @Column(name="_name")
    val roleName: RoleName,
){
    companion object {
        fun canSeeUserGroupData(roleName: String): Boolean {
            val role = RoleName.valueOf(roleName.removePrefix("ROLE_"))
            return role == RoleName.ADMIN || role == RoleName.SCIENTIST || role == RoleName.TEACHER
        }
        fun canSeeUserGroupData(role: RoleName): Boolean {
            return role == RoleName.ADMIN || role == RoleName.SCIENTIST || role == RoleName.TEACHER
        }
        fun canGetContacts(role: RoleName): Boolean {
            return role == RoleName.ADMIN || role == RoleName.SCIENTIST || role == RoleName.TEACHER || role == RoleName.PARENT
        }
    }
}


