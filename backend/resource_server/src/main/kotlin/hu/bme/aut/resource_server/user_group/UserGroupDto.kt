package hu.bme.aut.resource_server.user_group

abstract class UserGroupDto(
        val id: Int? = null,
        val name: String,
        val adminUsernames: List<String>? = emptyList()
) {
}