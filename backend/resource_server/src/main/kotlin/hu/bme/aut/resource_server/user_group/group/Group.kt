package hu.bme.aut.resource_server.user_group.group

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.UserGroup
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.organization.Organization
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("group")
class Group(
    name: String,

    @ManyToOne(cascade = [CascadeType.REFRESH, CascadeType.MERGE])
    @JoinColumn(name = "organization_id", referencedColumnName = "id")
    val organization: Organization,


    @ManyToMany
    @JoinTable(
        name = "group_member",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    override val members: MutableSet<UserEntity> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL])
    @JoinColumn(name = "parent_group_id", referencedColumnName = "id")
    var childGroups: MutableList<Group> = mutableListOf(),

    ) : UserGroup(name = name) {
    override fun getAllGroups(): List<Group> {
        val allGroupsInGroup = childGroups.toMutableList()
        childGroups.forEach { allGroupsInGroup.addAll(it.getAllGroups()) }
        return allGroupsInGroup
    }

    override fun getAllUserIds(): Set<Int> {
        val allUsersInGroup = members.map { it.id!! }.toMutableSet()
        childGroups.forEach { allUsersInGroup.addAll(it.getAllUserIds()) }
        return allUsersInGroup
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Group) return false

        if (id != other.id || name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return organization.hashCode() + name.hashCode() + id.hashCode()
    }

    override fun toDto(): UserGroupDto {
        return GroupDto(
            id = id!!,
            name = name,
            organizationDto = organization.toDto()
        )
    }

}
