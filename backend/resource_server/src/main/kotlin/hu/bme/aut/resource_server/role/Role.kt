package hu.bme.aut.resource_server.role

import hu.bme.aut.resource_server.utils.RoleName
import jakarta.persistence.*

@Entity
data class Role(
    @Id
    @Enumerated(value= EnumType.STRING)
    @Column(name="_name")
    val roleName: RoleName,
)


