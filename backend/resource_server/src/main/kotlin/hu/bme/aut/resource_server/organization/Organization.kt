package hu.bme.aut.resource_server.organization

import hu.bme.aut.resource_server.group.Group
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*

@Entity
data class Organization(
        @Id
        @GeneratedValue
        var id: Int? = null,

        @Column(name = "_name")
        val name: String,

        @Embedded
        @AttributeOverrides(
                AttributeOverride(name = "houseNumber", column = Column(name = "address_house_number")),
                AttributeOverride(name = "street", column = Column(name = "address_street")),
                AttributeOverride(name = "city", column = Column(name = "address_city")),
                AttributeOverride(name = "zip", column = Column(name = "address_zip"))
        )
        val address: Address,

        @ManyToMany
        @JoinTable(
                name="user_organization",
                joinColumns = [JoinColumn(name="organization_id")],
                inverseJoinColumns = [JoinColumn(name="user_id")]
        )
        val users: List<UserEntity> = mutableListOf(),

        @OneToMany(mappedBy = "organization")
        val groups: List<Group> = mutableListOf()


)
