package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
data class FloatProfileSnapshotItem (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name = "ability_id")
    override val abilityEntity: AbilityEntity,

    @Column
    override val abilityValue: Double,

        @CreationTimestamp
    @Column(name = "_timestamp")
    override val timestamp: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    override val user: UserEntity,
) : ProfileSnapshotItem() {

    constructor(profile: FloatProfileItem, user: UserEntity): this(
        abilityEntity = profile.ability,
        user = user,
        abilityValue = profile.abilityValue
    )
}