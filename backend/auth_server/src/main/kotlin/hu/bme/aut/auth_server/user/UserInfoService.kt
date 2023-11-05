package hu.bme.aut.auth_server.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.stereotype.Service

@Service
class UserInfoService(
    @Autowired private var userRepository: UserRepository,
) {
    fun loadUserInfoByUsername(username: String): OidcUserInfo {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("No user with username: $username found.")
        return OidcUserInfo.builder()
            .subject(username)
            .email(user.get().email)
            .familyName(user.get().firstName)
            .givenName(user.get().lastName)
            .claim("roles", user.get().roles.map { it.roleName })
            .build()
    }

    fun loadUserByUsername(username: String) = userRepository.findByUsername(username)

    fun existsContact(username: String, contactUsername: String): Boolean {
        val contacts = userRepository.getContactsByUsername(username)
        val contactEntity = userRepository.findByUsername(contactUsername)
        if (contactEntity.isEmpty) return false
        return contacts.any { contact -> contact.username == contactUsername && contact.id == contactEntity.get().id }
    }

    fun hasImpersonationRole(username: String): Boolean {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("No user with username: $username")
        return user.get().roles.any { it.isImpersonationRole() }

    }

    fun existsByUsername(username: String) = userRepository.existsByUsername(username)
}
