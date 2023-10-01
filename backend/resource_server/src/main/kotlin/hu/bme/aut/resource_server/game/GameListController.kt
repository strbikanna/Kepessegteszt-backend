package hu.bme.aut.auth_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/games")
class UserInfoController(
    @Autowired private var userInfoService: UserInfoService
) {
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'SCIENTIST', 'PARENT')")
    @GetMapping("/contacts")
    fun getContacts(authentication: Authentication): ResponseEntity<List<UserDao>> {
        val username = authentication.name
        val contacts = userInfoService.getContactDaos(username)
        return ResponseEntity(contacts, HttpStatus.OK)
    }

    @GetMapping("/me")
    fun getUserInfo(authentication: Authentication): ResponseEntity<UserDao> {
        val username = authentication.name
        return ResponseEntity(userInfoService.getUserDao(username), HttpStatus.OK)
    }

    @GetMapping("/status")
    fun status(): ResponseEntity<String> {
        return ResponseEntity(HttpStatus.OK)
    }
}