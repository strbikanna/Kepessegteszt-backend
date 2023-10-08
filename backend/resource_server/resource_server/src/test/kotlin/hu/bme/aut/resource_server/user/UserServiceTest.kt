package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.ProfileItem
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest(
    @Autowired private var userService: UserService,
    @Autowired private var testService: TestUtilsService,
) {
    @BeforeEach
    fun clearDb(){
        testService.emptyRepositories()
    }
    @Test
    fun shouldGetAllUsers(){
        testService.fillUserRepository()
        val users = userService.getAllUsers()
        assertEquals(2, users.size)
    }
    @Test
    fun shouldGetByUsername(){
        testService.fillUserRepository()
        val testUser1 = userService.getUserByUsername("test_user1")
        assertEquals("test_user1", testUser1.username)
        assertEquals("Test", testUser1.firstName)
        assertEquals("User", testUser1.lastName)
    }
    @Test
    fun shouldUpdateUser(){
        testService.fillUserRepository()
        val testUser1 = userService.getUserByUsername("test_user1")
        testUser1.firstName = "Updated"
        userService.updateUser(testUser1)
        val updatedUser = userService.getUserByUsername("test_user1")
        assertEquals("Updated", updatedUser.firstName)
        assertEquals(testUser1.lastName, updatedUser.lastName)
        assertEquals(testUser1.username, updatedUser.username)
    }

    @Test
    fun shouldAddItemToUserProfile(){
        testService.fillUserRepository()
        val testUser1 = userService.getUserWithProfileByUsername("test_user1")
        assertEquals(2, testUser1.profile.size)
        testUser1.profile.add(ProfileItem(ability = testService.abilityGsm, abilityValue = 5))
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(3, updatedUser.profile.size)
    }

    @Test
    fun shouldUpdateUserProfile(){
        testService.fillUserRepository()
        val testUser1 = userService.getUserWithProfileByUsername("test_user1")
        assertEquals(2, testUser1.profile.size)
        testUser1.profile.forEach{
            if(it.ability.code == testService.abilityGq.code){
                it.abilityValue = 6
                return@forEach
            }
        }
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(2, updatedUser.profile.size)
        val updatedProfileItem = updatedUser.profile.find { it.ability.code == testService.abilityGq.code }!!
        assertEquals(6, updatedProfileItem.abilityValue)
    }
    @Test
    fun shouldDeleteProfileItem(){
        testService.fillUserRepository()
        val testUser1 = userService.getUserWithProfileByUsername("test_user1")
        testUser1.profile.removeIf { it.ability.code == testService.abilityGq.code }
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(1, updatedUser.profile.size)
        assertEquals(testService.abilityGf, updatedUser.profile.first().ability)
    }
}