package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.group.Group
import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "group_type")
abstract class UserGroup(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Int? = null,

    @Column(name = "_name")
    open val name: String,

    @ManyToMany
    @JoinTable(
        name = "group_admin",
        joinColumns = [JoinColumn(name = "group_id")],
        inverseJoinColumns = [JoinColumn(name = "user_id")]
    )
    open val admins: MutableList<UserEntity> = mutableListOf(),

    ) {
    abstract val members: MutableList<UserEntity>
    abstract fun getAllGroups(): List<Group>
    abstract fun getAllUserIds(): Set<Int>
    abstract fun toDto(): UserGroupDto
}
