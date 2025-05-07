package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.profile.description_text.ProfileDescriptionService
import hu.bme.aut.resource_server.profile.description_text.ProfileDescriptionTextDto
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/profile_description")
class ProfileDescriptionController(
    private val profileDescriptionService: ProfileDescriptionService
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAbilitiesAsText(
        authentication: Authentication,
        @RequestParam(required = false) requestedUsername: String?,
        @RequestParam(required = false) prompt: String = "",
    ): ProfileDescriptionTextDto {
        val username = requestedUsername ?: authentication.name
        return if (prompt.isBlank())
            profileDescriptionService.getProfileDescriptionOfUser(username) else
            profileDescriptionService.generateProfileDescriptionOfUser(username, prompt)
    }

    @PostMapping("/compared-to-group")
    @ResponseStatus(HttpStatus.OK)
    suspend fun getAbilitiesAsTextToGroup(
        authentication: Authentication,
        @RequestParam(required = false) requestedUsername: String?,
        @RequestParam(required = false) userGroupId: Int?,
        @RequestParam(required = false) prompt: String = "",
        @RequestBody(required = false) filterDto: UserFilterDto?
    ): ProfileDescriptionTextDto {
        val username = requestedUsername ?: authentication.name
        return profileDescriptionService.generateComparisonTextToGroup(
            username,
            userGroupId,
            prompt,
            filterDto
        )
    }
}