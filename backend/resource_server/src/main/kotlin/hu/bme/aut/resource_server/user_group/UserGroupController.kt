package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user_group.group.GroupDto
import hu.bme.aut.resource_server.user_group.organization.OrganizationDto
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/user_group")
class UserGroupController(
    @Autowired private var userGroupService: UserGroupService,
    @Autowired private var authService: AuthService,
    ) {

    @GetMapping("/{groupType}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getAllUserGroups(
        @PathVariable groupType: String = "all",
        @RequestParam("pageIndex", required = false, defaultValue = "0") pageIndex: Int,
        @RequestParam("pageSize", required = false, defaultValue = "100") pageSize: Int,
        authentication: Authentication
    ): List<UserGroupDto> {
        val username = authentication.name
        return when (groupType) {
            "organization" -> userGroupService.getAllOrganizations(pageIndex, pageSize, username).map { it.toDto() }
            "org" -> userGroupService.getAllOrganizations(pageIndex, pageSize, username).map { it.toDto() }
            "group" -> userGroupService.getAllGroups(pageIndex, pageSize, username).map { it.toDto() }
            else -> userGroupService.getAllUserGroups(pageIndex, pageSize, username).map { it.toDto() }
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getUserGroupById(
        authentication: Authentication,
        @RequestParam id: Int
    ): UserGroupDto {
        authService.checkGroupDataReadAndThrow(authentication, id)
        return userGroupService.getById(id).toDto()
    }


    @GetMapping("/child_groups/{groupType}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getChildGroupsOfOrganization(
        @RequestParam("id") id: Int,
        @PathVariable groupType: String ,
    ): List<UserGroupDto> {
        return when (groupType) {
            "organization" -> userGroupService.getChildrenOfOrganization(id).map { it.toDto() }
            "org" -> userGroupService.getChildrenOfOrganization(id).map { it.toDto() }
            "group" -> userGroupService.getChildrenOfGroup(id).map { it.toDto() }
            else -> throw IllegalArgumentException("Invalid group type")
        }
    }

    @GetMapping("/search/{groupType}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun searchUserGroups(
        @PathVariable groupType: String = "all",
        @RequestParam name: String,
    ): List<UserGroupDto> {
        return when (groupType) {
            "organization" -> userGroupService.searchOrganizationsByName(name).map { it.toDto() }
            "org" -> userGroupService.searchOrganizationsByName(name).map { it.toDto() }
            "group" -> userGroupService.searchGroupsByName(name).map { it.toDto() }
            else -> userGroupService.searchOrganizationsByName(name).map { it.toDto() } +
                    userGroupService.searchGroupsByName(name).map { it.toDto() }
        }
    }

    @GetMapping("/members/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    fun getMembersOfGroup(
        authentication: Authentication,
        @PathVariable groupId: Int
    ): List<PlainUserDto> {
        authService.checkGroupDataReadAndThrow(authentication, groupId)
        return userGroupService.getAllUsersInGroup(groupId).map { PlainUserDto(it) }
    }

    @GetMapping("/users_to_see")
    @ResponseStatus(HttpStatus.OK)
    fun getAllUsersToSee(
        authentication: Authentication,
        @RequestParam(required = false, defaultValue = "0") pageIndex: Int,
        @RequestParam(required = false, defaultValue = "100") pageSize: Int
    ): List<PlainUserDto> {
        return userGroupService.getAllUsersToSee(authentication.name, pageIndex, pageSize).map { PlainUserDto(it) }
    }

    @DeleteMapping("/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeUserFromGroup(
        authentication: Authentication,
        @RequestParam("username") username: String,
        @RequestParam("groupId") groupId: Int
    ) {
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        userGroupService.removeUserFromGroup(username, groupId)
    }

    @GetMapping("/admins/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAdminsOfGroup(
        authentication: Authentication,
        @PathVariable groupId: Int
    ): List<PlainUserDto> {
        authService.checkGroupDataReadAndThrow(authentication, groupId)
        return userGroupService.getAdminsOfGroup(groupId).map { PlainUserDto(it) }
    }

    @PatchMapping("/admins")
    @ResponseStatus(HttpStatus.OK)
    fun addAdminUserToGroup(
        authentication: Authentication,
        @RequestParam("username") username: String,
        @RequestParam("groupId") groupId: Int
    ) {
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        userGroupService.addAdminUserToGroup(username, groupId)
    }

    @DeleteMapping("/admins")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeAdminFromGroup(
        authentication: Authentication,
        @RequestParam("username") username: String,
        @RequestParam("groupId") groupId: Int
    ) {
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        userGroupService.removeAdminFromGroup(username, groupId)
    }

    @PostMapping("/group")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    fun createGroup(
        authentication: Authentication,
        @RequestBody group: GroupDto
    ): UserGroupDto {
        authService.checkUserGroupWriteAndThrow(authentication, group.organizationDto.id)
        return userGroupService.createGroup(group.name, group.organizationDto.id).toDto()
    }

    @PostMapping("/organization")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    fun createOrganization(
        authentication: Authentication,
        @RequestBody org: OrganizationDto
    ): UserGroupDto {
        return userGroupService.createOrganization(org.name, org.address).toDto()
    }

    @PostMapping("/members/search")
    @ResponseStatus(HttpStatus.OK)
    fun searchMembersInGroup(
        authentication: Authentication,
        @RequestBody groupIds: List<Int>,
        @RequestParam("name") name: String
    ): Set<PlainUserDto> {
        groupIds.forEach { authService.checkGroupDataReadAndThrow(authentication, it) }
        val members: MutableSet<PlainUserDto> = mutableSetOf()
        members.addAll(userGroupService.searchOrganizationMembersByName(groupIds, name).map { PlainUserDto(it) })
        members.addAll(userGroupService.searchGroupMembersByName(groupIds, name).map { PlainUserDto(it) })
        return members
    }




}