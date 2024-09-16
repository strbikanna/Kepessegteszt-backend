package hu.bme.aut.resource_server.user.filter

data class UserFilterDto(
    val ageMin: Int? = null,
    val ageMax: Int? = null,
    val addressCity: String? = null,
    val addressZip: String? = null,
    val userGroupId: Int? = null,
    val abilityCode: String? = null,
    val abilityValueMin: Double? = null,
    val abilityValueMax: Double? = null,
)
