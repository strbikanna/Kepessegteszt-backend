package hu.bme.aut.resource_server.role

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.math.BigDecimal

@Entity
data class Subscription(
        @Id
        @Column(name = "_name")
        val name: String,

        var fee: BigDecimal,

        @OneToOne
        @JoinColumn(name = "role_name", referencedColumnName = "_name")
        val role: Role
)
