package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.dto.ProfileItem
import hu.bme.aut.resource_server.recommendation.special_settings.SpecialSettings
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user.role.Subscription
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import hu.bme.aut.resource_server.utils.Gender
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "user")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "first_name")
    val firstName: String,

    @Column(name = "last_name")
    val lastName: String,

    val username: String,

    @Column(name = "birth_date")
    val birthDate: LocalDate? = null,

    @Enumerated(EnumType.STRING)
    val gender: Gender? = null,

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
    var profileFloat: MutableSet<FloatProfileItem> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "user_id")
    var profileEnum: MutableSet<EnumProfileItem> = mutableSetOf(),

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

    @ManyToMany(mappedBy = "members")
    var organizations: MutableSet<Organization> = mutableSetOf(),

    @ManyToMany(mappedBy = "members")
    val groups: MutableSet<Group> = mutableSetOf(),

    @ElementCollection
    @CollectionTable(name = "special_game_settings", joinColumns = [JoinColumn(name = "fk_user_id")])
    var specialGameSettings: MutableSet<SpecialSettings> = mutableSetOf(),

    @Column(name = "xp")
    var xP: Int = 0

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

        return id == userEntity.id || username == userEntity.username
    }

    override fun hashCode(): Int {
        return id ?: username.hashCode()
    }
}
