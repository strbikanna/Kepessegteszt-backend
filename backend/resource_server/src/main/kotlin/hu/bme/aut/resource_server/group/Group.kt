package hu.bme.aut.resource_server.group

import hu.bme.aut.resource_server.organization.Organization
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*

@Entity
data class Group(
        @Id
        @GeneratedValue
        var id: Int? = null,

        val name: String,

        @ManyToOne
        @JoinColumn(name = "organization_id", referencedColumnName = "id")
        val organization: Organization,

        @ManyToOne
        @JoinColumn(name = "parent_group_id", referencedColumnName = "id")
        var parentGroup: Group? = null,

        @ManyToMany
        @JoinTable(
                name="user_group",
                joinColumns = [JoinColumn(name="group_id")],
                inverseJoinColumns = [JoinColumn(name="user_id")]
        )
        val users: List<UserEntity> = mutableListOf()
)
