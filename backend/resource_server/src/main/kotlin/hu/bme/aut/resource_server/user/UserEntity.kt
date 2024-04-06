package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.role.Subscription
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user")
data class UserEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,

        val firstName: String,

        val lastName: String,

        val username: String,

        val birthDate: LocalDate? = null,

        @Embedded
        @AttributeOverrides(
                AttributeOverride(name = "houseNumber", column = Column(name = "address_house_number")),
                AttributeOverride(name = "street", column = Column(name = "address_street")),
                AttributeOverride(name = "city", column = Column(name = "address_city")),
                AttributeOverride(name = "zip", column = Column(name = "address_zip"))
        )
        val address: Address? = null,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "user_id")
        var profileFloat: MutableSet<FloatProfileItem>,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
        @JoinColumn(name = "user_id")
        var profileEnum: MutableSet<EnumProfileItem>,

        @ManyToMany
        @JoinTable(
                name = "role_to_user",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "_name")],
        )
        val roles: MutableSet<Role>,

        @ManyToOne
        @JoinColumn(name = "subscription", referencedColumnName = "_name")
        var subscription: Subscription? = null,

        @ManyToMany
        @JoinTable(
                name = "org_member",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "org_id", referencedColumnName = "id")],
        )
        var organizations: MutableList<Organization> = mutableListOf(),

        @ManyToMany
        @JoinTable(
                name = "group_member",
                joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
                inverseJoinColumns = [JoinColumn(name = "group_id", referencedColumnName = "id")],
        )
        val groups: MutableList<Group> = mutableListOf(),

        ) {
    fun getProfile(): MutableSet<ProfileItem> {
        val profile = mutableSetOf<ProfileItem>()
        profile.addAll(profileFloat.map { it.toProfileItem() })
        profile.addAll(profileEnum.map { it.toProfileItem() })
        return profile
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false

        val userEntity = other as UserEntity

        return id == userEntity.id
    }
}
