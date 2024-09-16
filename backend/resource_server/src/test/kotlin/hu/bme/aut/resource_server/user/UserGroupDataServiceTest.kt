package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class UserGroupDataServiceTest(
    @Autowired private var userGroupDataService: UserGroupDataService,
    @Autowired private var testService: TestUtilsService
) {

    @BeforeEach
    fun setUp() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
        saveUsers()
    }

    @AfterEach
    fun clearDb() {
        testService.emptyRepositories()
    }

    @Test
    @Transactional
    fun shouldAddUserToGroup() {
        val org = Organization(name = "TestOrg", address = Address("11", "TestStreet", "TestCity", "1123"))
        val group = Group(name = "TestGroup", organization = org)
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        testService.organizationRepository.save(org)
        testService.groupRepository.save(group)
        userGroupDataService.addUserToGroup(user.username, group.id!!)
        val userInDb = testService.userRepository.findByUsername(user.username).orElseThrow()
        assertEquals(1, userInDb.groups.size)
    }

    @Test
    fun testEmptyUserGroups() {
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        val groups = userGroupDataService.getGroupsOfUser(user.username)
        assertEquals(0, groups.size)
    }

    @Test
    fun `should filter users by min and max age correctly`() {
        val userIdsFound = userGroupDataService.getAllUserIdsByFilter(UserFilterDto(ageMin = 10, ageMax = 15))
        assertEquals(3, userIdsFound.size)
    }

    @Test
    fun `should filter users by address correctly`() {
        val userIdsFound = userGroupDataService.getAllUserIdsByFilter(UserFilterDto(addressCity = "Budapest"))
        assertEquals(2, userIdsFound.size)
    }

    @Test
    fun `should filter users by ability correctly`() {
        var userIdsFound = userGroupDataService.getAllUserIdsByFilter(
            UserFilterDto(
                abilityCode = "Gf",
                abilityValueMin = 1.0,
                abilityValueMax = 1.1
            )
        )
        assertEquals(1, userIdsFound.size)
        userIdsFound = userGroupDataService.getAllUserIdsByFilter(
            UserFilterDto(
                abilityCode = "Gf",
                abilityValueMin = 0.8,
                abilityValueMax = 1.0
            )
        )
        assertEquals(2, userIdsFound.size)
    }

    @Test
    fun `should filter by address and age correctly`() {
        var userIdsFound = userGroupDataService.getAllUserIdsByFilter(
            UserFilterDto(
                addressCity = "Budapest",
                ageMin = 10,
                ageMax = 15
            )
        )
        assertEquals(2, userIdsFound.size)
        userIdsFound =
            userGroupDataService.getAllUserIdsByFilter(UserFilterDto(addressCity = "Pecs", ageMin = 12, ageMax = 16))
        assertEquals(1, userIdsFound.size)
    }

    @Test
    fun `should filter users by address and ability correctly`() {
        val userIdsFound = userGroupDataService.getAllUserIdsByFilter(
            UserFilterDto(
                addressCity = "Budapest",
                abilityCode = "Gf",
                abilityValueMin = 1.0,
                abilityValueMax = 1.2
            )
        )
        assertEquals(2, userIdsFound.size)
    }

    private fun saveUsers() {
        val user1 = testService.createUnsavedTestUser().copy(
            username = "test_user1",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("11a", "Petofi utca", "Budapest", "1111")
        )
        user1.profileFloat.forEach { it.abilityValue = 1.0 }

        val user2 = testService.createUnsavedTestUser().copy(
            username = "test_user2",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("112", "Petofi utca", "Budapest", "1021")
        )
        user2.profileFloat.forEach { it.abilityValue = 1.2 }

        val user3 = testService.createUnsavedTestUser().copy(
            username = "test_user3",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("110", "Bethlen koz", "Pecs", "8011")
        )
        user3.profileFloat.forEach { it.abilityValue = 0.9 }

        testService.userRepository.saveAll(listOf(user1, user2, user3))
    }

}