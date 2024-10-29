package hu.bme.aut.resource_server.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.dto.ProfileItem
import jakarta.persistence.*

/**
 * Entity class for cognitive profile items with float values.
 * A collection of these items represents a cognitive profile.
 */
@Entity
data class FloatProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name="ability_id", referencedColumnName = "code")
    val ability: AbilityEntity,

    @Column
    var abilityValue: Double
){
    fun toProfileItem(): ProfileItem {
        return ProfileItem(
            ability = ability,
            value = abilityValue
        )
    }
}
