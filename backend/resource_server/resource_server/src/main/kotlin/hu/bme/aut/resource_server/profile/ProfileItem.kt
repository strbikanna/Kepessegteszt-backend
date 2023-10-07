package hu.bme.aut.resource_server.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.Ability
import jakarta.persistence.*

@Entity
data class ProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name="ability_id")
    val ability: Ability,

    @Column
    val abilityValue: Int
)
