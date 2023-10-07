package hu.bme.aut.resource_server.user

class PlainUserDto(
    val firstName: String,

    val lastName: String,

    val username: String,
) {
    constructor(user: UserEntity) : this(
        user.firstName,
        user.lastName,
        user.username,
    )

}