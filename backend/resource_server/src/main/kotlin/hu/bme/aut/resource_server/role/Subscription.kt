package hu.bme.aut.resource_server.role

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.math.BigDecimal

@Entity
data class Subscription(
        @Id
        val name: String,

        var fee: BigDecimal,

        @OneToOne
        @JoinColumn(name = "role_name", referencedColumnName = "_name")
        val role: Role
)
