package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileRepository
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@SpringBootTest
class UserServiceTest(
    @Autowired private var userService: UserService,
    @Autowired private var testService: TestUtilsService,
    @Autowired private var profileRepository: FloatProfileRepository,
) {
    @BeforeEach
    fun clearDb() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }

    @Test
    fun shouldDeleteProfileItemsWithUser() {
        testService.fillUserRepository();
        assertEquals(4L, profileRepository.count())
        testService.userRepository.deleteAll()
        assertEquals(0L, testService.userRepository.count())
        assertEquals(0L, profileRepository.count())
        assertDoesNotThrow { testService.abilityRepository.deleteAll() }
    }

    @Test
    fun shouldGetAllUsers() {
        testService.fillUserRepository()
        val users = userService.getAllUsers()
        assertEquals(2, users.size)
    }

    @Test
    fun shouldGetByUsername() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserDtoByUsername("test_user1")
        assertEquals("test_user1", testUser1.username)
        assertEquals("Test", testUser1.firstName)
        assertEquals("User", testUser1.lastName)
    }

    @Test
    fun shouldUpdateUser() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserDtoByUsername("test_user1")
        testUser1.firstName = "Updated"
        userService.updateUser(testUser1)
        val updatedUser = userService.getUserDtoByUsername("test_user1")
        assertEquals("Updated", updatedUser.firstName)
        assertEquals(testUser1.lastName, updatedUser.lastName)
        assertEquals(testUser1.username, updatedUser.username)
    }

    @Test
    fun shouldAddItemToUserProfile() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserEntityWithProfileByUsername("test_user1")
        assertEquals(2, testUser1.profileFloat.size)
        testUser1.profileFloat.add(FloatProfileItem(ability = testService.abilityGsm, abilityValue = 5.0))
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(3, updatedUser.profile.size)
    }

    @Test
    fun shouldUpdateUserProfile() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserEntityWithProfileByUsername("test_user1")
        assertEquals(2, testUser1.profileFloat.size)
        testUser1.profileFloat.forEach {
            if (it.ability.code == testService.abilityGq.code) {
                it.abilityValue = 6.0
                return@forEach
            }
        }
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(2, updatedUser.profile.size)
        val updatedProfileItem = updatedUser.profile.find { it.ability.code == testService.abilityGq.code }!!
        assertEquals(6.0, updatedProfileItem.value)
    }

    @Test
    fun shouldDeleteProfileItem() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserEntityWithProfileByUsername("test_user1")
        testUser1.profileFloat.removeIf { it.ability.code == testService.abilityGq.code }
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(1, updatedUser.profile.size)
        assertEquals(testService.abilityGf, updatedUser.profile.first().ability)
    }

    @Test
    fun shouldAddMultipleProfileItems() {
        testService.fillUserRepository()
        val testUser1 = userService.getUserEntityWithProfileByUsername("test_user1")
        assertEquals(2, testUser1.profileFloat.size)
        testUser1.profileFloat.add(FloatProfileItem(ability = testService.abilityGsm, abilityValue = 5.0))
        testUser1.profileEnum.add(
            EnumProfileItem(
                ability = testService.abilityColorsense,
                abilityValue = EnumAbilityValue.POSSIBLE
            )
        )
        val updatedUser = userService.updateUserProfile(testUser1)
        assertEquals(4, updatedUser.profile.size)
    }

    @Test
    @Transactional
    fun shouldDeleteUser(){
        testService.fillUserRepository()
        val organization = Organization(name = "Test org", address = Address(street = "Test street", city = "Test city", zip = "1234", houseNumber = "1"))
        testService.organizationRepository.save(organization)
        val testUser1 = userService.getUserEntityWithProfileByUsername("test_user1")
        testUser1.organizations.add(organization)
        testService.userRepository.save(testUser1)
        testService.resultRepository.save(
            testService.createGamePlayResult(testUser1)
        )
        assertEquals(1, testService.resultRepository.findAllByUser(testUser1).size)
        userService.removeUserForever("test_user1")
        assertFalse(testService.userRepository.existsByUsername("test_user1"))
        assertEquals(0, testService.resultRepository.findAllByUser(testUser1).size)
    }


}