package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.RoleEntity
import jakarta.persistence.*

@Entity
@Table(name = "USERS")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    val email: String,

    val firstName: String,

    val lastName: String,

    val username: String,

    val password: String,

    var enabled: Boolean,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "USER_ROLES",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "roleName")],
    )
    val roles: MutableSet<RoleEntity>,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "CONTACTS",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "contact_id", referencedColumnName = "id")],
    )
    var contacts: MutableList<UserEntity>,
)
