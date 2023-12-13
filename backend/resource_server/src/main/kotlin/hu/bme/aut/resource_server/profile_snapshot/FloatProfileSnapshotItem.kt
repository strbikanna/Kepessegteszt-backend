package hu.bme.aut.resource_server.profile_snapshot

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

/**
 * Entity class that is a snapshot of a user's cognitive profile item with float value
 * at given time.
 */
@Entity
data class FloatProfileSnapshotItem (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    override var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name = "ability_id")
    override val ability: AbilityEntity,

    @Column
    override val abilityValue: Double,

    @CreationTimestamp
    @Column(name = "_timestamp")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    override val timestamp: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    override val user: UserEntity,
) : ProfileSnapshotItem() {

    constructor(profile: FloatProfileItem, user: UserEntity): this(
        ability = profile.ability,
        user = user,
        abilityValue = profile.abilityValue
    )
}