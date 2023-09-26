package hu.bme.aut.auth_server

import hu.bme.aut.auth_server.user.UserRepository
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

}