package hu.bme.aut.resource_server.user

import jakarta.persistence.*

@Entity
data class UserEntity(
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    var id: Int? = null,

    val firstName: String,

    val lastName: String,

    val username: String,

    @OneToMany
    @JoinColumn(name="user_id")
    val profile: Set<ProfileItem>,

    @ManyToMany
    @JoinTable(
        name = "role_to_user",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
    )
    val roles: Set<Role>,
)
