package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.user_group.UserGroupDto
import kotlinx.coroutines.Deferred
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    @Autowired private var userService: UserService,
    @Autowired private var userGroupService: UserGroupDataService,
    @Autowired private var authService: AuthService
) {
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(authentication: Authentication): List<ProfileItem> {
        val username = authentication.name
        return userService.getUserDtoWithProfileByUsername(username).profile.toList()
    }

    @GetMapping("/profile/inspect")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT')")
    fun getOtherUserProfile(authentication: Authentication, @RequestParam(required = true) username: String): Deferred<UserProfileDto>
    = authService.doIfIsContact(authentication, username) {
        userService.getUserDtoWithProfileByUsername(username)
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    fun getGroupsOfUser(authentication: Authentication): List<UserGroupDto>{
        val username = authentication.name
        return userGroupService.getGroupsOfUser(username)
    }

    @PutMapping("/group")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun addUserToGroup(authentication: Authentication, @RequestParam(required = true) username: String, @RequestParam(required = true) groupId: Int){
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        userGroupService.addUserToGroup(username, groupId)
    }

    /**
     * @param aggregationMode: "average" | "sum" | "max" | "min"
     */
    @PostMapping("/group_profile/aggregate")
    @ResponseStatus(HttpStatus.OK)
    fun compareProfileToGroupData(
        authentication: Authentication,
        @RequestParam(required = false) userGroupId: Int?,
        @RequestParam(required = false) aggregationMode: String = "average",
        @RequestBody(required = false) filterDto: UserFilterDto?
    ): List<ProfileItem>{
        userGroupId?.let{authService.checkGroupDataReadAndThrow(authentication, userGroupId)}
        val user = userService.getUserEntityWithProfileByUsername(authentication.name)
        val abilities = user.profileFloat.map { it.ability }.toSet()
        return when(aggregationMode){
            "average" -> userGroupService.getAbilityToAverageValueInGroup(userGroupId, filterDto, abilities)
            "sum" -> userGroupService.getAbilityToSumValueInGroup(userGroupId, filterDto, abilities)
            "max" -> userGroupService.getAbilityToMaxValueInGroup(userGroupId, filterDto, abilities)
            "min" -> userGroupService.getAbilityToMinValueInGroup(userGroupId, filterDto, abilities)
            else -> throw IllegalArgumentException("Invalid aggregation mode, supported modes: average, sum, max, min")
        }
    }

    @GetMapping("/group_profile/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_ADMIN')")
    fun getGroupProfile(authentication: Authentication, @RequestParam(required = true) groupId: Int): List<ProfileItem>{
        authService.checkGroupDataReadAndThrow(authentication, groupId)
        val user = userService.getUserEntityWithProfileByUsername(authentication.name)
        val abilities = user.profileFloat.map { it.ability }
        val abilityToValues = mutableMapOf<AbilityEntity, List<Double>>()
        abilities.forEach {
            val values = userGroupService.getAbilityValuesInUserGroupAscending(groupId, it.code)
            abilityToValues[it] = values
        }
        return abilityToValues.map { ProfileItem( it.key, it.value) }
    }

}