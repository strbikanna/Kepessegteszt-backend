package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.ProfileItem

class UserProfileDto(
    firstName: String,

    lastName: String,

    username: String,

    val profile: MutableSet<ProfileItem>,

) : PlainUserDto(firstName, lastName, username) {
    constructor(user: UserEntity) : this(
        user.firstName,
        user.lastName,
        user.username,
        user.profile,
    )

}