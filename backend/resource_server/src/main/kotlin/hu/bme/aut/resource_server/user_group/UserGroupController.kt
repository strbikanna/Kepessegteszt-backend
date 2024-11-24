package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.UserGroupDataService
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user_group.group.GroupDto
import hu.bme.aut.resource_server.user_group.organization.OrganizationDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAllUserGroups(): List<UserGroupDto> {
        return userGroupService.getAllUserGroups().map { it.toDto() }
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

    @GetMapping("/admins/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    fun getAdminsOfGroup(
        authentication: Authentication,
        @PathVariable groupId: Int
    ): List<PlainUserDto> {
        authService.checkGroupDataReadAndThrow(authentication, groupId)
        return userGroupService.getAdminsOfGroup(groupId).map { PlainUserDto(it) }
    }

    @PutMapping("/admin_to_group")
    @ResponseStatus(HttpStatus.OK)
    fun addAdminUserToGroup(
        authentication: Authentication,
        @RequestParam("username") username: String,
        @RequestParam("groupId") groupId: Int
    ) {
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        userGroupService.addAdminUserToGroup(username, groupId)
    }

    @DeleteMapping("/admin_from_group")
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
    fun createOrganization(
        authentication: Authentication,
        @RequestBody org: OrganizationDto
    ): UserGroupDto {
        return userGroupService.createOrganization(org.name, org.address).toDto()
    }




}