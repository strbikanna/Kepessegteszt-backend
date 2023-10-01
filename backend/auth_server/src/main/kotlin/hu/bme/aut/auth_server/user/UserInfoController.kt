package hu.bme.aut.auth_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
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

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(@RequestParam pageNumber: Int?, @RequestParam pageSize: Int?): List<UserDao> {
        return userInfoService.getUsersWithoutContact(pageNumber ?: 0, pageSize ?: 1)
    }

    @GetMapping("/status")
    fun statusTest(): ResponseEntity<String> {
        return ResponseEntity(HttpStatus.OK)
    }
}