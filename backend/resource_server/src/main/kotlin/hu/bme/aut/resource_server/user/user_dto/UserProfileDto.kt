package hu.bme.aut.resource_server.user.user_dto

import hu.bme.aut.resource_server.profile.dto.ProfileItem
import hu.bme.aut.resource_server.user.UserEntity

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
        user.getProfile(),
    )

}