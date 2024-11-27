package hu.bme.aut.resource_server.user_group.group

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.organization.OrganizationDto

class GroupDto(
        id: Int?,
        name: String,
        adminUsernames: List<String>? = emptyList(),

        @JsonProperty(value="organization")
        val organizationDto: OrganizationDto,

        val childGroupIds: List<Int> = emptyList()
) :UserGroupDto(id, name, adminUsernames){
}