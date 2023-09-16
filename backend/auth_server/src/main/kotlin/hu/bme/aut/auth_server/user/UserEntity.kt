package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.RoleEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table

@Entity
@Table(name = "USERS")
data class UserEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val firstName: String,

    val lastName: String,

    val email: String,

    val username: String,

    val password: String,

    @ManyToMany
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
    )
    val  roleEntities: Set<RoleEntity>,

    @ManyToMany
    @JoinTable(
        name = "CONTACTS",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "contact_id", referencedColumnName = "id")],
    )
    val  contacts: Set<UserEntity>,
)
