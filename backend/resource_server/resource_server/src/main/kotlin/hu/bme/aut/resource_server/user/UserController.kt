package hu.bme.aut.resource_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(@Autowired private var userService: UserService) {

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(authentication: Authentication): UserProfileDto {
        val username = authentication.name
        return userService.getUserWithProfileByUsername(username)
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    fun updateUserProfile(@RequestBody userProfileDto: UserProfileDto) {
        userService.updateUserProfile(userProfileDto)
    }
}