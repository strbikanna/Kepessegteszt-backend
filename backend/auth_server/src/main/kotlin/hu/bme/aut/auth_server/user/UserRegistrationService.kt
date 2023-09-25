package hu.bme.aut.auth_server.user

import hu.bme.aut.auth_server.RegistrationData
import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.role.RoleEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserRegistrationService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var passwordEncoder: PasswordEncoder
) {
    fun saveUser(userData: RegistrationData) {
        val encodedPassword = passwordEncoder.encode(userData.password)
        val userEntity = UserEntity(
            firstName = userData.firstName,
            lastName = userData.lastName,
            email = userData.email,
            username = userData.username,
            password = encodedPassword,
            roles = mutableSetOf(RoleEntity(roleName = mapRole(userData.role))),
            contacts = mutableSetOf(),
            enabled = true
        )
        userRepository.save(userEntity)
    }

    private fun mapRole(role: String): Role {
        return when (role.uppercase()) {
            "TEACHER" -> Role.TEACHER_REQUEST
            "STUDENT" -> Role.STUDENT_REQUEST
            "SCIENTIST" -> Role.SCIENTIST_REQUEST
            "PARENT" -> Role.PARENT_REQUEST
            else -> {
                Role.STUDENT_REQUEST
            }
        }
    }
}