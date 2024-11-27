package hu.bme.aut.resource_server.user_group.organization

import hu.bme.aut.resource_server.user_group.UserGroupDto

class OrganizationDto(
        id: Int?,
        name: String,
        adminUsernames: List<String>? = emptyList(),
        val address: Address,
) : UserGroupDto(id, name, adminUsernames) {
}