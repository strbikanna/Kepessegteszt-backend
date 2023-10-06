package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.RegistrationData
import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.role.RoleEntity
import hu.bme.aut.auth_server.role.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserRegistrationService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var passwordEncoder: PasswordEncoder,
    @Autowired private var roleRepository: RoleRepository
) {
    fun saveUserOrThrowException(userData: RegistrationData): UserEntity {
        val encodedPassword = passwordEncoder.encode(userData.password)
        val userEntity = UserEntity(
            firstName = userData.firstName,
            lastName = userData.lastName,
            email = userData.email,
            username = userData.username,
            password = encodedPassword,
            roles = mutableSetOf(mapRole(userData.role)),
            contacts = mutableSetOf(),
            enabled = false
        )
        userRepository.save(userEntity)
        return userEntity
    }

    private fun mapRole(role: String): RoleEntity {
        val roleName = when (role.uppercase()) {
            "TEACHER" -> Role.TEACHER_REQUEST
            "STUDENT" -> Role.STUDENT
            "SCIENTIST" -> Role.SCIENTIST_REQUEST
            "PARENT" -> Role.PARENT_REQUEST
            else -> {
                Role.STUDENT
            }
        }
        return roleRepository.findByRoleName(roleName)
    }
}