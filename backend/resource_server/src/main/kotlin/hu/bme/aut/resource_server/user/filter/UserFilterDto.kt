package hu.bme.aut.resource_server.user.filter

data class UserFilterDto(
    val ageMin: Int? = null,
    val ageMax: Int? = null,
    val addressCity: String? = null,
    val addressZip: String? = null,
    val userGroupId: Int? = null,
    val abilityFilter: List<AbilityFilterDto> = emptyList(),
)

data class AbilityFilterDto(
    val code: String? = null,
    val valueMin: Double? = null,
    val valueMax: Double? = null,
)
