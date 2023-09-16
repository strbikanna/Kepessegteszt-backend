package hu.bme.aut.auth_server.role

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "ROLES")
data class RoleEntity(
    @Id
    val id: Int,
    val roleName: String
)
