package hu.bme.aut.resource_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(@Autowired private var userService: UserService){
}