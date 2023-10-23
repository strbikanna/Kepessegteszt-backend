package hu.bme.aut.auth_server.repository

import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.role.RoleEntity
import hu.bme.aut.auth_server.user.UserEntity
import hu.bme.aut.auth_server.user.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserRepositoryTest(@Autowired private var userRepository: UserRepository) {

    @Test
    fun shouldReturnContact() {
        val result = userRepository.getContactsByUsername("teacher_user")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun shouldFindUser() {
        val result = userRepository.findByUsername("student_user")
    }

    @Test
    fun shouldSaveUser() {
        val userEntity = UserEntity(
            firstName = "firstName",
            lastName = "lastName",
            email = "email",
            username = "username",
            password = "encodedPassword",
            roles = mutableSetOf(RoleEntity(roleName = Role.PARENT_REQUEST)),
            contacts = mutableListOf(),
            enabled = true
        )
        userRepository.save(userEntity)
        assertNotNull(userEntity.id)
    }


}