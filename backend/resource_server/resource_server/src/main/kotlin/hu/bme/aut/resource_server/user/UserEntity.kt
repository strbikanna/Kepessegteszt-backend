package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.user.role.Role
import jakarta.persistence.*

@Entity
@Table(name="user")
data class UserEntity(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int? = null,

    val firstName: String,

    val lastName: String,

    val username: String,

    @OneToMany
    @JoinColumn(name="user_id")
    val profile: MutableSet<ProfileItem>,

    @ManyToMany
    @JoinTable(
        name = "role_to_user",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "_name")],
    )
    val roles: Set<Role>,
)
