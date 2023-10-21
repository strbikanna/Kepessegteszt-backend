package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.Role

data class UserDto(
    var id: Int?,

    val email: String,

    val firstName: String,

    val lastName: String,

    val username: String,

    val roles: MutableSet<Role>,

    val contacts: MutableSet<UserDto> = mutableSetOf(),
)