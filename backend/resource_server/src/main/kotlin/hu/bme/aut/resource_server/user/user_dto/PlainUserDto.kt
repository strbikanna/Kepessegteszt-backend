package hu.bme.aut.resource_server.user.user_dto

import com.fasterxml.jackson.annotation.JsonFormat
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.utils.Gender
import java.time.LocalDate

open class PlainUserDto(
    var firstName: String,

    var lastName: String,

    val username: String,

    val address: Address?,

    @JsonFormat(pattern="dd-MM-yyyy")
    val birthDate: LocalDate?,

    val gender: Gender?
) {
    constructor(user: UserEntity) : this(
        user.firstName,
        user.lastName,
        user.username,
        user.address,
        user.birthDate,
        user.gender
    )

}