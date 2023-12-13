package hu.bme.aut.auth_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserManagementController(
    @Autowired private var userService: UserManagementService
) {
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'SCIENTIST', 'PARENT')")
    @GetMapping("/impersonation_contacts")
    fun getContacts(authentication: Authentication): ResponseEntity<List<UserDto>> {
        val username = authentication.name
        val contacts = userService.getImpersonationContactDtos(username)
        return ResponseEntity(contacts, HttpStatus.OK)
    }

    @GetMapping("/me")
    fun getUserInfo(authentication: Authentication): ResponseEntity<UserDto> {
        val username = authentication.name
        return ResponseEntity(userService.getUserDto(username), HttpStatus.OK)
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getAll(@RequestParam pageNumber: Int?, @RequestParam pageSize: Int?): List<UserDto> {
        return userService.getUsersWithoutContact(pageNumber, pageSize)
    }

    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllCount(): Long {
        return userService.getUsersCount()
    }

    @GetMapping("/{id}/contacts")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun getContactsOfUser(@PathVariable id: Int): List<UserDto> {
        val user = userService.loadUserById(id).orElseThrow()
        return userService.getContactDtos(user.username)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUser(@PathVariable id: Int, @RequestBody user: UserDto): ResponseEntity<UserDto> {
        if(user.id==null){
            user.id = id
        }
        return ResponseEntity(userService.updateUser(user), HttpStatus.OK)
    }

    @GetMapping("/status")
    fun statusTest(): ResponseEntity<String> {
        return ResponseEntity(HttpStatus.OK)
    }
}