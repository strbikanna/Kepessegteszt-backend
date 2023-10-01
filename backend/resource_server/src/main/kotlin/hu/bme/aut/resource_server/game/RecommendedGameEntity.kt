package hu.bme.aut.resource_server.game

import jakarta.persistence.*

@Entity
@Table(name = "GAMES")
data class GameEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val game_name: String,

    val game_description: String

    val icon: String,

    val game_actuve: Boolean,

    val url: String,

    val json_descriptor: String
)

package hu.bme.aut.resource_server.game

import jakarta.persistence.*

@Entity
@Table(name = "GAMES")
data class GameEntity(
        @Id
        @GeneratedValue
        val id: Int,

        val game_name: String,

        val game_description: String

        val icon: String,

        val game_actuve: Boolean,

        val url: String,

        val json_descriptor: String
)



@OneToMany(fetch = FetchType.LAZY)
@JoinTable(
        name = "",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
)
val roleEntities: MutableSet<RoleEntity>,

@ManyToMany(fetch = FetchType.LAZY)
@JoinTable(
        name = "CONTACTS",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "contact_id", referencedColumnName = "id")],
)
val contacts: MutableSet<UserEntity>,