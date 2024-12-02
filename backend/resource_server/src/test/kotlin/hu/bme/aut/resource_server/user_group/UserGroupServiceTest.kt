package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import hu.bme.aut.resource_server.utils.RoleName
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles


@SpringBootTest
@ActiveProfiles("test")
class UserGroupServiceTest(
    @Autowired private var testUtilsService: TestUtilsService,
    @Autowired private var userGroupService: UserGroupService,
    @Autowired private var groupRepository: GroupRepository,
    @Autowired private var organizationRepository: OrganizationRepository
) {
    private lateinit var authUser: UserEntity

    @BeforeEach
    fun setUp() {
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
        authUser = testUtilsService.createUnsavedTestUser()
        authUser.roles.add(Role(RoleName.ADMIN))
        testUtilsService.saveUser(authUser)
    }

    @Test
    fun shouldSaveGroup() {
        val org = createOrganization(1)
        val group = createGroup(1, org)
        organizationRepository.save(org)
        groupRepository.save(group)
        val savedGroup = groupRepository.findAll().first()
        assertNotNull(savedGroup.id)
        assertNotNull(savedGroup.organization.id)
    }

    @Test
    fun shouldGetAllGroupsAndOrgs() {
        val org = createOrganization(1)
        val group1 = createGroup(1, org)
        val group2 = createGroup(2, org)
        organizationRepository.save(org)
        groupRepository.save(group1)
        groupRepository.save(group2)
        val allGroups = userGroupService.getAllUserGroups(authUsername = authUser.username)
        assertEquals(3, allGroups.size)
    }

    @Test
    @Transactional
    fun shouldGetAllNestedGroups() {
        val org = createOrganization(1)
        val group1 = createGroup(1, org)
        val group2 = createGroup(2, org)
        val group3 = createGroup(3, org)
        group1.childGroups.add(group2)
        group2.childGroups.add(group3)
        organizationRepository.save(org)
        groupRepository.save(group1)
        val allGroups = userGroupService.getAllUserGroups(authUsername = authUser.username)
        assertEquals(4, allGroups.size)
    }

    @Test
    @Transactional
    fun shouldRemoveUserFromGroup() {
        val org = createOrganization(1)
        val group1 = createGroup(1, org)
        val user = testUtilsService.createUnsavedTestUser().copy(username = "test_user2")
        testUtilsService.saveUser(user)
        group1.members.add(user)
        org.groups.add(group1)
        organizationRepository.save(org)
        groupRepository.save(group1)
        userGroupService.removeUserFromGroup(user.username, org.id!!)
        val allUsersInGroup = userGroupService.getAllUsersInGroup(group1.id!!)
        val modifiedUser = testUtilsService.userRepository.findById(user.id!!)
        assertEquals(0, allUsersInGroup.size)
        assertEquals(0, modifiedUser.get().groups.size)
    }

    @Test
    @Transactional
    fun shouldRemoveAdminFromGroup() {
        val org = createOrganization(1)
        val group1 = createGroup(1, org)
        val user = testUtilsService.createUnsavedTestUser().copy(username = "test_user2")
        testUtilsService.saveUser(user)
        group1.admins.add(user)
        org.groups.add(group1)
        org.members.add(user)
        org.admins.add(user)
        organizationRepository.save(org)
        groupRepository.save(group1)
        userGroupService.removeAdminFromGroup(user.username, org.id!!)
        val orgInDb = organizationRepository.findAll().first()
        assertEquals(0, orgInDb.admins.size)
        assertEquals(1, orgInDb.groups.first().admins.size)
        assertEquals(1, orgInDb.members.size)
    }

    private fun createGroup(testNum: Int, organization: Organization): Group {
        return Group(name = "Example Group $testNum", organization = organization)
    }

    private fun createOrganization(testNum: Int): Organization {
        return Organization(name = "Example School $testNum", address = createAddress(testNum))
    }

    private fun createAddress(houseNum: Int): Address {
        return Address(
            city = "Budapest",
            zip = "1117",
            street = "Irinyi József utca",
            houseNumber = "$houseNum"
        )
    }
}