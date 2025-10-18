package hu.bme.aut.resource_server.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.dto.ProfileItem
import jakarta.persistence.*
import java.math.BigDecimal

/**
 * Entity class for cognitive profile items with float values.
 * A collection of these items represents a cognitive profile.
 */
@Entity(name = "float_profile_item")
data class FloatProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name="ability_id", referencedColumnName = "code")
    val ability: AbilityEntity,

    @Column(name = "ability_value")
    var abilityValue: Double,

    /**
     * The accuracy of the ability value.
     * @max 1.0
     * @min 0.0
     */
    @Column(name = "ability_accuracy")
    var abilityAccuracy: Double = 0.0
){
    fun toProfileItem(): ProfileItem {
        return ProfileItem(
            ability = ability,
            value = abilityValue,
            accuracy = abilityAccuracy
        )
    }
}
