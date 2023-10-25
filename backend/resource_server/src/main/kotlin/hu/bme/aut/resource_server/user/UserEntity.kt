package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.role.Role
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

    @OneToMany(cascade=[CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name="user_id")
    var profileFloat: MutableSet<FloatProfileItem>,

    @OneToMany(cascade=[CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name="user_id")
    var profileEnum: MutableSet<EnumProfileItem>,

    @ManyToMany
    @JoinTable(
        name = "role_to_user",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "_name")],
    )
    val roles: Set<Role>,
){
    fun getProfile(): MutableSet<ProfileItem>{
        val profile = mutableSetOf<ProfileItem>()
        profile.addAll(profileFloat)
        profile.addAll(profileEnum)
        return profile
    }
}
