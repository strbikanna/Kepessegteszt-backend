package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.role.RoleRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class for managing users.
 */
@Service
class UserManagementService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var roleRepository: RoleRepository,
) {
    fun loadUserById(id: Int) = userRepository.findById(id)
    fun loadUserByUsername(username: String) = userRepository.findByUsername(username)
    fun save(userEntity: UserEntity) = userRepository.save(userEntity)
    @Transactional
    fun loadUserByUsernameWithContacts(username: String): Optional<UserEntity> {
        val user = userRepository.findByUsername(username)
        if (user.isEmpty) return user
        val contacts = userRepository.getContactsByUsername(username)
        user.get().contacts = mutableListOf()
        user.get().contacts.addAll(contacts)
        return user
    }
    fun getUserDto(username: String): UserDto {
        val userEntity = userRepository.findByUsername(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val existingUser = userEntity.get()
        return convertUserDto(existingUser)
    }

    /**
     * Returns the contacts of the user that are students (aka can be impersonated).
     */
    fun getImpersonationContactDtos(username: String): List<UserDto> {
        val userEntity = loadUserByUsernameWithContacts(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val contacts = userEntity.get().contacts
        return contacts
            .filter { it.roles.any { roleEntity -> roleEntity.roleName == Role.STUDENT } }
            .map { convertUserDto(it) }
    }

    /**
     * Returns all contacts of the user.
     */
    fun getContactDtos(username: String): List<UserDto> {
        val userEntity = loadUserByUsernameWithContacts(username)
        if (userEntity.isEmpty) throw UsernameNotFoundException("Invalid username: $username")
        val contacts = userEntity.get().contacts
        return contacts.map { convertUserDto(it) }
    }
    fun getUsersWithoutContact(pageNumber: Int?, pageSize: Int?): List<UserDto> {
        val users = if(pageNumber == null || pageSize == null){
            userRepository.findAll()
        }else{
            userRepository.findAll(PageRequest.of(pageNumber, pageSize)).content
        }
        return users.map { entity -> convertUserDto(entity) }
    }
    fun getUsersWithoutContactByName(nameString: String): List<UserDto> {
        val users = userRepository.findByFirstNameLikeOrLastNameLike("%${nameString}%", "%${nameString}%")
        return users.map { entity -> convertUserDto(entity) }
    }
    fun getUsersCount(): Long {
        return userRepository.count()
    }
    fun updateUser(userDto: UserDto): UserDto {
        val userEntity = userRepository.findById(userDto.id!!).orElseThrow()
        val roles = roleRepository.findByRoleNameIn(userDto.roles)
        updateContactBothSide(userEntity, userDto.contacts)
        val contacts = userRepository.findByIdIn(userDto.contacts.map { it.id!! }.toSet())
        val updatedUserEntity = userEntity.copy(
            email = userDto.email,
            firstName = userDto.firstName,
            lastName = userDto.lastName,
            roles = roles.toMutableSet(),
            contacts = contacts.toMutableList(),
        )
        val savedUserEntity = userRepository.save(updatedUserEntity)
        return convertUserDto(savedUserEntity)
    }

    /**
     * Updates the contacts of the user and the contacts' contacts.
     */
    @Transactional
    fun updateContactBothSide(dbUser: UserEntity, updatedContactList: List<UserDto>){
        val dbContacts = userRepository.getContactsByUsername(dbUser.username)
        dbContacts.forEach{dbContact ->
            if(!updatedContactList.any{updatedContact -> updatedContact.id == dbContact.id || updatedContact.username == dbContact.username}){
                dbContact.contacts.removeIf { it.id == dbUser.id }
                userRepository.save(dbContact)
            }
        }
        val contacts = userRepository.findByIdIn(updatedContactList.map { it.id!! }.toSet())
        userRepository.save(dbUser.copy(contacts = contacts.toMutableList()))

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