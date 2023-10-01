package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.RoleEntity
import jakarta.persistence.*

@Entity
@Table(name = "USERS")
data class UserEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val email: String,

    val firstName: String,

    val lastName: String,

    val username: String,

    val password: String,

    val enabled: Boolean,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
    )
    val roleEntities: MutableSet<RoleEntity>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "CONTACTS",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "contact_id", referencedColumnName = "id")],
    )
    val contacts: MutableSet<UserEntity>,
)
