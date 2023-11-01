package hu.bme.aut.resource_server.profile

import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.AbilityEntity
import jakarta.persistence.*

@Entity
data class FloatProfileItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    override var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name="ability_id", referencedColumnName = "code")
    override val ability: AbilityEntity,

    @Column
    override var abilityValue: Double
): ProfileItem()
