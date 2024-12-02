package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.user.filter.AbilityFilterDto
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
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

    @Test
    fun shouldAddUserToGroup() {
        val org = Organization(name = "TestOrg", address = Address("11", "TestStreet", "TestCity", "1123"))
        val group = Group(name = "TestGroup", organization = org)
        val user = testService.createUnsavedTestUser()
        testService.saveUser(user)
        testService.organizationRepository.save(org)
        testService.groupRepository.save(group)
        userGroupDataService.addUserToGroup(user.username, group.id!!)
        val userInDb = testService.userRepository.findByUsername(user.username).orElseThrow()
        val dbGroup = testService.groupRepository.findById(group.id!!).orElseThrow()
        val dbOrg = testService.organizationRepository.findById(org.id!!).orElseThrow()
        assertEquals(1, userInDb.groups.size)
        assertEquals(1, userInDb.organizations.size)
        assertEquals(1, dbGroup.members.size)
        assertEquals(1, dbOrg.members.size)
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
        val userIdsFound = userGroupDataService.getAllUserIdsByFilter(
            UserFilterDto(
                abilityFilter = listOf( AbilityFilterDto(code = "Cls"))
            )
        )
        assertEquals(1, userIdsFound.size)
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
                abilityFilter = listOf(
                    AbilityFilterDto(
                        code = "Cls",
                        valueMin = 1.0,
                        valueMax = 1.2
                    )
                )
            )
        )
        assertEquals(1, userIdsFound.size)
    }

    private fun saveUsers() {
        val user1 = testService.createUnsavedTestUser().copy(
            username = "test_user1",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("11a", "Petofi utca", "Budapest", "1111")
        )
        user1.profileEnum.add(EnumProfileItem(null, testService.abilityColorsense, EnumAbilityValue.YES))

        val user2 = testService.createUnsavedTestUser().copy(
            username = "test_user2",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("112", "Petofi utca", "Budapest", "1021")
        )
        user1.profileEnum.add(EnumProfileItem(null, testService.abilityColorsense, EnumAbilityValue.NO))

        val user3 = testService.createUnsavedTestUser().copy(
            username = "test_user3",
            birthDate = LocalDate.of(2010, 11, 2),
            address = Address("110", "Bethlen koz", "Pecs", "8011")
        )
        user1.profileEnum.add(EnumProfileItem(null, testService.abilityColorsense, EnumAbilityValue.UNKNOWN))

        testService.userRepository.saveAll(listOf(user1, user2, user3))
    }

}