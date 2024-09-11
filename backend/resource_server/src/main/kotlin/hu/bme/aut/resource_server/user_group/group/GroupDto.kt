package hu.bme.aut.resource_server.user_group.group

import hu.bme.aut.resource_server.user_group.UserGroupDto

class GroupDto(
        id: Int,
        name: String,
        val organizationDto: UserGroupDto
) :UserGroupDto(id, name){
}