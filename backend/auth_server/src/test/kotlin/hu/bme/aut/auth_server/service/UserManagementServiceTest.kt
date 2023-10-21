package hu.bme.aut.auth_server.service

import hu.bme.aut.auth_server.user.UserDto
import hu.bme.aut.auth_server.user.UserEntity
import hu.bme.aut.auth_server.user.UserManagementService
import hu.bme.aut.auth_server.user.UserRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserManagementServiceTest(
    @Autowired private var userManagementService: UserManagementService,
    @Autowired private var userRepository: UserRepository,
) {
    private var testUserEntity1 = UserEntity(
        username = "test_user1",
        firstName = "test_1",
        lastName = "user_1",
        email = "email_1",
        password = "encodedPassword_1",
        roles = mutableSetOf(),
        contacts = mutableListOf(),
        enabled = true
    )
    private var testUserEntity2 = UserEntity(
        username = "test_user2",
        firstName = "test_2",
        lastName = "user_2",
        email = "email_2",
        password = "encodedPassword_2",
        roles = mutableSetOf(),
        contacts = mutableListOf(testUserEntity1),
        enabled = true
    )
    @BeforeEach
    fun init(){
        userRepository.deleteAll()
        testUserEntity1 = userManagementService.save(testUserEntity1)
        testUserEntity2 = userManagementService.save(testUserEntity2)
    }

    @Test
    fun shouldExistContactBothSide(){
        val user1 = userManagementService.loadUserByUsernameWithContacts(testUserEntity1.username).get()
        val user2 = userManagementService.loadUserByUsernameWithContacts(testUserEntity2.username).get()
        assertTrue(user1.contacts.any { it.id == user2.id })
        assertTrue(user2.contacts.any { it.id == user1.id })
    }
    @Test
    fun shouldUpdateContactsBothSide(){
        var user3= UserEntity(
            username = "test_user3",
            firstName = "test_3",
            lastName = "user_3",
            email = "email_3",
            password = "encodedPassword_3",
            roles = mutableSetOf(),
            contacts = mutableListOf(),
            enabled = true
        )
        userManagementService.save(user3)
        //delete contact
        userManagementService.updateContactBothSide(testUserEntity1, listOf(
            UserDto(
            id = user3.id,
            username = user3.username,
            firstName = user3.firstName,
            lastName = user3.lastName,
            email = user3.email,
            roles = mutableSetOf(),
        )))
        val user1 = userManagementService.loadUserByUsernameWithContacts(testUserEntity1.username).get()
        val user2 = userManagementService.loadUserByUsernameWithContacts(testUserEntity2.username).get()
        user3 = userManagementService.loadUserByUsernameWithContacts(user3.username).get()
        assertTrue(user1.contacts.none { it.id == user2.id })
        assertTrue(user1.contacts.any { it.id == user3.id })
        assertTrue(user3.contacts.any { it.id == user1.id })
        assertTrue(user2.contacts.isEmpty())
    }
}