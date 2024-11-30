package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.llm.abilities2text.AbiltityToTextDto
import hu.bme.aut.resource_server.profile.dto.ProfileItem
import hu.bme.aut.resource_server.profile.dto.ProfileItemStatisticsDto
import hu.bme.aut.resource_server.profile_calculation.calculator.CalculationHelper
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user_group.UserGroupDto
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    @Autowired private var authService: AuthService,
    @Autowired private var abilitiesToTextService : hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
) {
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(authentication: Authentication): List<ProfileItem> {
        val username = authentication.name
        return userService.getUserDtoWithProfileByUsername(username).profile.toList()
    }

    @GetMapping("/profile/inspect")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT', 'ROLE_ADMIN')")
    fun getOtherUserProfile(
        authentication: Authentication,
        @RequestParam username: String
    ): Deferred<List<ProfileItem>> = authService.doIfIsContact(authentication, username) {
        userService.getUserDtoWithProfileByUsername(username).profile.toList()
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    fun getGroupsOfUser(authentication: Authentication): List<UserGroupDto> {
        val username = authentication.name
        return userGroupService.getGroupsOfUser(username)
    }

    @GetMapping("/groups/inspect")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT', 'ROLE_ADMIN')")
    fun getGroupsOfOtherUser(authentication: Authentication, @RequestParam username: String): List<UserGroupDto> {
        return userGroupService.getGroupsOfUser(username)
    }

    @PatchMapping("/{groupType}")
    @ResponseStatus(HttpStatus.OK)
    fun addUserToGroup(
        authentication: Authentication,
        @RequestParam(required = true) username: String,
        @RequestParam(required = true) groupId: Int,
        @PathVariable groupType: String
    ) {
        authService.checkUserGroupWriteAndThrow(authentication, groupId)
        when(groupType) {
            "group" -> userGroupService.addUserToGroup(username, groupId)
            "organization" -> userGroupService.addUserToOrganization(username, groupId)
            "org" -> userGroupService.addUserToOrganization(username, groupId)
            else -> throw IllegalArgumentException("Invalid group type, supported types: group, organization")
        }
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
    ): List<ProfileItem> {
        userGroupId?.let { authService.checkGroupDataReadAndThrow(authentication, userGroupId) }
        val user = userService.getUserEntityWithProfileByUsername(authentication.name)
        return getAggregateProfileOfGroup(user, aggregationMode, userGroupId, filterDto)
    }

    /**
     * @param aggregationMode: "average" | "sum" | "max" | "min"
     */
    @PostMapping("/group_profile/aggregate/inspect")
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    fun compareOtherUsersProfileToGroupData(
        @RequestParam username: String,
        @RequestParam(required = false) userGroupId: Int?,
        @RequestParam(required = false) aggregationMode: String = "average",
        @RequestBody(required = false) filterDto: UserFilterDto?
    ): List<ProfileItem> {
        val user = userService.getUserEntityWithProfileByUsername(username)
        return getAggregateProfileOfGroup(user, aggregationMode, userGroupId, filterDto)
    }

    private fun getAggregateProfileOfGroup(
        user: UserEntity,
        aggregationMode: String,
        userGroupId: Int?,
        filterDto: UserFilterDto?
    ): List<ProfileItem> {
        val abilities = user.profileFloat.map { it.ability }.toSet()
        return when (aggregationMode) {
            "average" -> userGroupService.getAbilityToAverageValueInGroup(userGroupId, filterDto, abilities)
            "sum" -> userGroupService.getAbilityToSumValueInGroup(userGroupId, filterDto, abilities)
            "max" -> userGroupService.getAbilityToMaxValueInGroup(userGroupId, filterDto, abilities)
            "min" -> userGroupService.getAbilityToMinValueInGroup(userGroupId, filterDto, abilities)
            else -> throw IllegalArgumentException("Invalid aggregation mode, supported modes: average, sum, max, min")
        }
    }

    @GetMapping("/profile/abilities-as-text")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAbilitiesAsText(
        authentication: Authentication,
        @RequestParam(required = false) requestedUsername: String?,
        @RequestParam(required = false) prompt: String = "",
    ): AbiltityToTextDto {
        val username = requestedUsername ?: authentication.name
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()
        if(userAbilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        // Should be called in a coroutine or a suspend function
        return abilitiesToTextService.generateFromAbilities(userAbilities, prompt)
    }

    @PostMapping("/profile/abilities-as-text-to-group")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAbilitiesAsTextToGroup(
        authentication: Authentication,
        @RequestParam(required = false) requestedUsername: String?,
        @RequestParam(required = false) userGroupId: Int?,
        @RequestParam(required = false) prompt: String = "",
        @RequestBody(required = false) filterDto: UserFilterDto?
    ): AbiltityToTextDto {
        val username = requestedUsername ?: authentication.name
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()

        val user = userService.getUserEntityWithProfileByUsername(username)
        val abilities = user.profileFloat.map { it.ability }.toSet()
        if(abilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        // IntelliJ IDEA suggests to put it in withContext(Dispatchers.IO) when called in a suspend function
        val groupAbilities = withContext(Dispatchers.IO) {
            userGroupService.getAbilityToAverageValueInGroup(userGroupId, filterDto, abilities)
        }
        if(groupAbilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        val groupName = userGroupId?.let { userGroupService.getGroupById(it).name } ?: "csoport"

        // Should be called in a coroutine or a suspend function
        return abilitiesToTextService.generateFromAbilitiesComparedToGroup(userAbilities, groupAbilities, groupName, prompt)
    }

    @GetMapping("/group_profile/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_ADMIN')")
    fun getGroupProfile(
        authentication: Authentication,
        @RequestParam(required = true) groupId: Int
    ): List<ProfileItem> {
        authService.checkGroupDataReadAndThrow(authentication, groupId)
        val user = userService.getUserEntityWithProfileByUsername(authentication.name)
        val abilities = user.profileFloat.map { it.ability }
        val abilityToValues = mutableMapOf<AbilityEntity, List<Double>>()
        abilities.forEach {
            val values = userGroupService.getAbilityValuesInUserGroupAscending(groupId, it.code)
            abilityToValues[it] = values
        }
        return abilityToValues.map { ProfileItem(it.key, it.value) }
    }

    @PostMapping("/group_profile/statistics")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_ADMIN', 'ROLE_TEACHER')")
    fun getGroupProfileStatistics(
        @RequestParam username: String,
        @RequestParam(required = false) userGroupId: Int?,
        @RequestBody(required = false) filterDto: UserFilterDto?
    ): List<ProfileItemStatisticsDto> {
        val user = userService.getUserEntityWithProfileByUsername(username)
        val abilities = user.profileFloat.map { it.ability }.toSet()
        val groupMean = userGroupService.getAbilityToAverageValueInGroup(userGroupId, filterDto, abilities)
        val abilityToValues = mutableMapOf<String, List<Double>>()
        abilities.forEach {
            val values = if (userGroupId != null) {
                userGroupService.getAbilityValuesInUserGroupAscending(userGroupId, it.code)
            } else {
                userGroupService.getAllValuesOfAbility(it.code)
            }
            abilityToValues[it.code] = values
        }
        val groupStatistics = abilityToValues.map { (abilityCode, values) ->
            val mean = groupMean.find { it.ability.code == abilityCode } ?: return@map null
            val stdDev = CalculationHelper.calculateStdDeviation(values, mean.value as Double)
            val userValue = user.profileFloat.find { it.ability.code == abilityCode }?.abilityValue ?: return@map null
            ProfileItemStatisticsDto(
                mean.ability,
                mean.value,
                stdDev,
                userValue
            )
        }.filterNotNull()
        return groupStatistics
    }

    @GetMapping("/personal_data")
    @ResponseStatus(HttpStatus.OK)
    fun getUserData(authentication: Authentication): PlainUserDto {
        val username = authentication.name
        return userService.getUserDtoByUsername(username)
    }

    @PutMapping("/personal_data")
    @ResponseStatus(HttpStatus.OK)
    fun updateUserData(
        authentication: Authentication,
        @RequestBody user: PlainUserDto
    ) {
        val username = authentication.name
        if (username != user.username) {
            throw IllegalArgumentException("You can only update your own data.")
        }
        userService.updateUser(user)
    }

}