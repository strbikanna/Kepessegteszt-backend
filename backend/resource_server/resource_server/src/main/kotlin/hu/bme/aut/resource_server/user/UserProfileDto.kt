package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile.ProfileItem

class UserProfileDto(
    val firstName: String,

    val lastName: String,

    val username: String,

    val profile: MutableSet<ProfileItem>,
) {
    constructor(user: UserEntity) : this(
        user.firstName,
        user.lastName,
        user.username,
        user.profile,
    )

}