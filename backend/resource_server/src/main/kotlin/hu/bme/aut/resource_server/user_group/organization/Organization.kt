package hu.bme.aut.resource_server.user_group.organization

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.UserGroup
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.group.Group
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("org")
class Organization(
        name: String,

        @Embedded
        @AttributeOverrides(
                AttributeOverride(name = "houseNumber", column = Column(name = "address_house_number")),
                AttributeOverride(name = "street", column = Column(name = "address_street")),
                AttributeOverride(name = "city", column = Column(name = "address_city")),
                AttributeOverride(name = "zip", column = Column(name = "address_zip"))
        )
        val address: Address,

        @OneToMany(mappedBy = "organization")
        val groups: MutableList<Group> = mutableListOf(),

        @ManyToMany(mappedBy = "organizations")
        override val members: MutableList<UserEntity> = mutableListOf()

) : UserGroup(name = name) {
    override fun getAllGroups(): List<Group> {
        val allGroupsInOrg = groups.toMutableList()
        groups.forEach { allGroupsInOrg.addAll(it.getAllGroups()) }
        return allGroupsInOrg
    }

    override fun getAllUserIds(): Set<Int> {
        val allUsersInOrg = members.map { it.id!! }.toMutableSet()
        groups.forEach { allUsersInOrg.addAll(it.getAllUserIds()) }
        return allUsersInOrg
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Organization) return false

        if (id == other.id) return true
        if (name != other.name) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        return address.hashCode() + name.hashCode() + id.hashCode()
    }

    override fun toDto(): UserGroupDto {
        return OrganizationDto(
                id = id!!,
                name = name,
                address = address
        )
    }
}
