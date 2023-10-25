package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
class EnumProfileSnapshotItem  (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long? = null,

    @ManyToOne(cascade=[CascadeType.REFRESH], fetch= FetchType.EAGER)
    @JoinColumn(name = "ability_id")
    override val abilityEntity: AbilityEntity,

    @CreationTimestamp
    @Column(name = "_timestamp")
    override val timestamp: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    override val user: UserEntity,

    @Column
    @Enumerated(EnumType.STRING)
    override val abilityValue: EnumAbilityValue,

        ) : ProfileSnapshotItem() {
        constructor(profile: EnumProfileItem, user: UserEntity): this(
            abilityEntity = profile.abilityEntity,
            user = user,
            abilityValue = profile.abilityValue
        )
}
