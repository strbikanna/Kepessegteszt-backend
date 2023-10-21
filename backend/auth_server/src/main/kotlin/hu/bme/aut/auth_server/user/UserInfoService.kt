package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.role.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.oidc.OidcUserInfo
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserInfoService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var roleRepository: RoleRepository,
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
    fun loadUserById(id: Int) = userRepository.findById(id)

    fun save(userEntity: UserEntity) = userRepository.save(userEntity)
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

    fun hasImpersonationRole(username: String): Boolean {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) throw UsernameNotFoundException("No user with username: $username")
        return user.get().roles.any { it.isMimicRole() }

    }

    fun existsByUsername(username: String) = userRepository.existsByUsername(username)

    fun getUserDto(username: String): UserDto {
        val userEntity = userRepository.findByUsername(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val existingUser = userEntity.get()
        return convertUserDto(existingUser)
    }

    fun getImpersonationContactDtos(username: String): List<UserDto> {
        val userEntity = loadUserByUsernameWithContacts(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val contacts = userEntity.get().contacts
        return contacts
            .filter { it.roles.any { roleEntity -> roleEntity.roleName == Role.STUDENT } }
            .map { convertUserDto(it) }
    }

    fun getContactDtos(username: String): List<UserDto> {
        val userEntity = loadUserByUsernameWithContacts(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val contacts = userEntity.get().contacts
        return contacts.map { convertUserDto(it) }
    }

    fun getUsersWithoutContact(pageNumber: Int?, pageSize: Int?): List<UserDto> {
        val users: List<UserEntity>
        if(pageNumber == null || pageSize == null){
            users = userRepository.findAll()
        }else{
            users = userRepository.findAll(PageRequest.of(pageNumber, pageSize)).content
        }
        return users.map { entity -> convertUserDto(entity) }
    }
    fun getUsersCount(): Long {
        return userRepository.count()
    }
    fun updateUser(userDto: UserDto): UserDto {
        val userEntity = userRepository.findById(userDto.id!!).orElseThrow()
        val roles = roleRepository.findByRoleNameIn(userDto.roles)
        val contacts = userRepository.findByIdIn(userDto.contacts.map { it.id!! }.toSet())
        val updatedUserEntity = userEntity.copy(
            email = userDto.email,
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            roles = roles.toMutableSet(),
            contacts = contacts.toMutableSet(),
        )
        val savedUserEntity = userRepository.save(updatedUserEntity)
        return convertUserDto(savedUserEntity)
    }


    private fun convertUserDto(userEntity: UserEntity): UserDto {
        return UserDto(
            id = userEntity.id,
            username = userEntity.username,
            email = userEntity.email,
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            roles = userEntity.roles.map { entity -> entity.roleName }.toMutableSet(),
        )
    }
}
