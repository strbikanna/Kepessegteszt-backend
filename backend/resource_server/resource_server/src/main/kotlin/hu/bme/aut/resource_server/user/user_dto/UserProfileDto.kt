package hu.bme.aut.resource_server.user.user_dto

import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.user.UserEntity

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
        user.getProfile(),
    )

}