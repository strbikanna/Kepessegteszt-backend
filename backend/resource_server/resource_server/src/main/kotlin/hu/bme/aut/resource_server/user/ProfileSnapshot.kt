package hu.bme.aut.resource_server.user

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
data class ProfileSnapshot (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @CreationTimestamp
    val timestamp: Instant,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: UserEntity,

    @OneToMany(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "snapshot_id")
    val profile: Set<ProfileSnapshotItem>,
)