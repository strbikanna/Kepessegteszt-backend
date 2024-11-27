package hu.bme.aut.resource_server.user_group.group

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.user_group.UserGroupDto

class GroupDto(
        id: Int,
        name: String,
        adminUsernames: List<String>,

        @JsonProperty(value="organization")
        val organizationDto: UserGroupDto,

        val childGroupIds: List<Int> = emptyList()
) :UserGroupDto(id, name, adminUsernames){
}