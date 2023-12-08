package hu.bme.aut.auth_server.service

import hu.bme.aut.auth_server.RegistrationData
import hu.bme.aut.auth_server.role.Role
import hu.bme.aut.auth_server.user.UserRegistrationService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class RegistrationServiceTest(
    @Autowired private var registrationService: UserRegistrationService
) {
    @Test
    fun shouldEncodePasswordWithBcrypt() {
        val data = RegistrationData(false)
        data.firstName = "Test"
        data.lastName = "User"
        data.email = "email"
        data.username = "username"
        data.password = "hashedValue##"
        data.role = Role.STUDENT.name
        val encodedPassword = registrationService.saveUserOrThrowException(data).password
        assertTrue(BCryptPasswordEncoder().matches("hashedValue##", encodedPassword))
    }
}