package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(@Autowired private var userService: UserService) {

    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    fun getUserProfile(authentication: Authentication): UserProfileDto {
        val username = authentication.name
        return userService.getUserDtoWithProfileByUsername(username)
    }

}