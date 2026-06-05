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
        val userIdsFound = userGroupDataService.getAllUserIdsByFilter(UserFilterDto(ageMin = 10, ageMax = 17))
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
                ageMax = 16
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

    @Test
    @Transactional
    fun `should get ability max values in group`() {
        // create two organizations and groups (A and B)
        val orgA = Organization(name = "OrgMaxA", address = Address("1", "a", "b", "0000"))
        val groupA = Group(name = "GroupMaxA", organization = orgA)
        testService.organizationRepository.save(orgA)
        val savedGroupA = testService.groupRepository.save(groupA)

        val orgB = Organization(name = "OrgMaxB", address = Address("2", "a", "b", "0001"))
        val groupB = Group(name = "GroupMaxB", organization = orgB)
        testService.organizationRepository.save(orgB)
        val savedGroupB = testService.groupRepository.save(groupB)

        // fetch existing users and modify their ability values
        val u1 = testService.userRepository.findByUsername("test_user1").get()
        val u2 = testService.userRepository.findByUsername("test_user2").get()
        val u3 = testService.userRepository.findByUsername("test_user3").get()

        val abilityCode = testService.abilityGf.code
        u1.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 2.0
        u2.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.5
        u3.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 1.5
        testService.userRepository.saveAll(listOf(u1, u2, u3))

        // add u1 and u2 to group A, u3 to group B
        userGroupDataService.addUserToGroup(u1.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u2.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u3.username, savedGroupB.id!!)

        val abilities = setOf(testService.abilityGf)
        val resultA = userGroupDataService.getAbilityToMaxValueInGroup(savedGroupA.id, null, abilities)
        val resultB = userGroupDataService.getAbilityToMaxValueInGroup(savedGroupB.id, null, abilities)

        assertEquals(1, resultA.size)
        assertEquals(1, resultB.size)
        val itemA = resultA.find { it.ability.code == abilityCode }
        val itemB = resultB.find { it.ability.code == abilityCode }
        // max in group A (u1=2.0, u2=0.5) should be 2.0
        assertEquals(2.0, itemA!!.value)
        // max in group B (u3=1.5) should be 1.5
        assertEquals(1.5, itemB!!.value)
    }

    @Test
    @Transactional
    fun `should get ability average values in group`() {
        val orgA = Organization(name = "OrgAvgA", address = Address("3", "a", "b", "0002"))
        val groupA = Group(name = "GroupAvgA", organization = orgA)
        testService.organizationRepository.save(orgA)
        val savedGroupA = testService.groupRepository.save(groupA)

        val orgB = Organization(name = "OrgAvgB", address = Address("4", "a", "b", "0003"))
        val groupB = Group(name = "GroupAvgB", organization = orgB)
        testService.organizationRepository.save(orgB)
        val savedGroupB = testService.groupRepository.save(groupB)

        val u1 = testService.userRepository.findByUsername("test_user1").get()
        val u2 = testService.userRepository.findByUsername("test_user2").get()
        val u3 = testService.userRepository.findByUsername("test_user3").get()

        val abilityCode = testService.abilityGf.code
        u1.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 2.0
        u2.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.0
        u3.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.5
        testService.userRepository.saveAll(listOf(u1, u2, u3))

        // add u1 and u2 to group A (avg 1.0), u3 to group B (avg 0.5)
        userGroupDataService.addUserToGroup(u1.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u2.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u3.username, savedGroupB.id!!)

        val abilities = setOf(testService.abilityGf)
        val resultA = userGroupDataService.getAbilityToAverageValueInGroup(savedGroupA.id, null, abilities)
        val resultB = userGroupDataService.getAbilityToAverageValueInGroup(savedGroupB.id, null, abilities)

        assertEquals(1, resultA.size)
        assertEquals(1, resultB.size)
        val itemA = resultA.find { it.ability.code == abilityCode }
        val itemB = resultB.find { it.ability.code == abilityCode }
        assertEquals(1.0, itemA!!.value)
        assertEquals(0.5, itemB!!.value)
    }

    @Test
    @Transactional
    fun `should get ability min values in group`() {
        val orgA = Organization(name = "OrgMinA", address = Address("5", "a", "b", "0004"))
        val groupA = Group(name = "GroupMinA", organization = orgA)
        testService.organizationRepository.save(orgA)
        val savedGroupA = testService.groupRepository.save(groupA)

        val orgB = Organization(name = "OrgMinB", address = Address("6", "a", "b", "0005"))
        val groupB = Group(name = "GroupMinB", organization = orgB)
        testService.organizationRepository.save(orgB)
        val savedGroupB = testService.groupRepository.save(groupB)

        val u1 = testService.userRepository.findByUsername("test_user1").get()
        val u2 = testService.userRepository.findByUsername("test_user2").get()
        val u3 = testService.userRepository.findByUsername("test_user3").get()

        val abilityCode = testService.abilityGf.code
        u1.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.4
        u2.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.8
        u3.profileFloat.find { it.ability.code == abilityCode }!!.abilityValue = 0.2
        testService.userRepository.saveAll(listOf(u1, u2, u3))

        // add u1 and u2 to A (min 0.4), u3 to B (min 0.2)
        userGroupDataService.addUserToGroup(u1.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u2.username, savedGroupA.id!!)
        userGroupDataService.addUserToGroup(u3.username, savedGroupB.id!!)

        val abilities = setOf(testService.abilityGf)
        val resultA = userGroupDataService.getAbilityToMinValueInGroup(savedGroupA.id, null, abilities)
        val resultB = userGroupDataService.getAbilityToMinValueInGroup(savedGroupB.id, null, abilities)

        assertEquals(1, resultA.size)
        assertEquals(1, resultB.size)
        val itemA = resultA.find { it.ability.code == abilityCode }
        val itemB = resultB.find { it.ability.code == abilityCode }
        assertEquals(0.4, itemA!!.value)
        assertEquals(0.2, itemB!!.value)
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