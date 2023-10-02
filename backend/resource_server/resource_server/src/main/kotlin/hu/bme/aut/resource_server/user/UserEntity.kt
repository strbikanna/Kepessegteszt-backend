package hu.bme.aut.resource_server.user

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class UserEntity(
    @Id
    @GeneratedValue
    var id: Int? = null,

)
