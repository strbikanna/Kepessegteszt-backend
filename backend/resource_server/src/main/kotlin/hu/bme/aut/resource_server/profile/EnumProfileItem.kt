package hu.bme.aut.resource_server.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import jakarta.persistence.*

/**
 * Entity class for profile items with enum value (non-discrete).
 */
@Entity
data class EnumProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    override var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name="ability_id", referencedColumnName = "code")
    override val ability: AbilityEntity,

    @Column
    @Enumerated(value= EnumType.STRING)
    override var abilityValue: EnumAbilityValue
): ProfileItem()