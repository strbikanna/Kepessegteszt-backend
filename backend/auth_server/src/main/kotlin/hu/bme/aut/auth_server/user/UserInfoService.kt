package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserInfoService(@Autowired private var userRepository: UserRepository) {
    fun loadUserInfoByUsername(username: String): OidcUserInfo {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("No user with username: $username found.")
        return OidcUserInfo.builder()
            .subject(username)
            .email(user.get().email)
            .familyName(user.get().firstName)
            .givenName(user.get().lastName)
            .claim("roles", user.get().roleEntities.map { it.roleName })
            .build()
    }

    fun loadUserByUsername(username: String) = userRepository.findByUsername(username)

    fun loadUserByUsernameWithContacts(username: String): Optional<UserEntity> {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) return user
        val contacts = userRepository.getContactsByUsername(username)
        user.get().contacts.addAll(contacts)
        return user
    }

    fun existsContact(username: String, contactUsername: String): Boolean {
        val contacts = userRepository.getContactsByUsername(username)
        val contactEntity = userRepository.findByUsername(contactUsername)
        if (contactEntity.isEmpty) return false
        return contacts.any { contact -> contact.username == contactUsername && contact.id == contactEntity.get().id }
    }

    fun hasMimicRole(username: String): Boolean {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("No user with username: $username")
        return user.get().roleEntities.any { it.isMimicRole() }

    }

    fun existsByUsername(username: String) = userRepository.existsByUsername(username)

    fun getUserDao(username: String): UserDao {
        val userEntity = userRepository.findByUsername(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val existingUser = userEntity.get()
        return convertUserDao(existingUser)
    }

    fun getContactDaos(username: String): List<UserDao> {
        val userEntity = loadUserByUsernameWithContacts(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val contacts = userEntity.get().contacts
        return contacts
            .filter { it.roleEntities.any { roleEntity -> roleEntity.roleName == Role.STUDENT } }
            .map { convertUserDao(it) }
    }

    private fun convertUserDao(userEntity: UserEntity): UserDao {
        return UserDao(
            username = userEntity.username,
            email = userEntity.email,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            roles = userEntity.roleEntities.map { entity -> entity.roleName }.toMutableSet(),
        )
    }
}
