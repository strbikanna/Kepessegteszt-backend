package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.ability.Ability
import jakarta.persistence.*

@Entity
data class ProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.PERSIST])
    @JoinColumn(name="ability_id")
    val ability: Ability,

    @Column
    val abilityValue: Int
)
