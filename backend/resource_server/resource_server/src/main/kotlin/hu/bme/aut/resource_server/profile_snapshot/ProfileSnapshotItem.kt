package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.Ability
import jakarta.persistence.*

@Entity
data class ProfileSnapshotItem (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.PERSIST])
    @JoinColumn(name = "ability_id")
    val ability: Ability,

    @Column
    val abilityValue: Int,
)