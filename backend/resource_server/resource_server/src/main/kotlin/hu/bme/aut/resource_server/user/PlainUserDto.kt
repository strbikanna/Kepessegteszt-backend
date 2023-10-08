package hu.bme.aut.resource_server.user

open class PlainUserDto(
    var firstName: String,

    var lastName: String,

    val username: String,
) {
    constructor(user: UserEntity) : this(
        user.firstName,
        user.lastName,
        user.username,
    )

}