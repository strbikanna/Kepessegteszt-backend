package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    @Autowired private var userService: UserService,
    @Autowired private var authService: AuthService
) {

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(authentication: Authentication): UserProfileDto {
        val username = authentication.name
        return userService.getUserDtoWithProfileByUsername(username)
    }

    @GetMapping("/profile/inspect")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT')")
    fun getOtherUserProfile(authentication: Authentication, @RequestParam(required = true) username: String): UserProfileDto {
        authService.checkContactAndThrow(authentication, username)
        return userService.getUserDtoWithProfileByUsername(username)
    }

}