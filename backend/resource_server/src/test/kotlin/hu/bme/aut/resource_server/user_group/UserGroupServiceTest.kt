package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.user.role.Role
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
import org.junit.jupiter.api.assertThrows
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
        val allUsersInGroup = userGroupService.getAllUsersInGroup(group1.id!!,0, 100)
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

    @Test
    @Transactional
    fun shouldAddAdminToGroup() {
        val org = createOrganization(10)
        val group = createGroup(10, org)
        val user = testUtilsService.createUnsavedTestUser().copy(username = "new_admin")
        testUtilsService.saveUser(user)
        organizationRepository.save(org)
        groupRepository.save(group)
        // add admin
        userGroupService.addAdminUserToGroup(user.username, group.id!!)
        val groupInDb = groupRepository.findById(group.id!!).get()
        // admin should be present
        assertEquals(1, groupInDb.admins.size)
        assertEquals(user.username, groupInDb.admins.first().username)
    }

    @Test
    fun shouldGetAllOrganizationsForAdmin() {
        val org1 = createOrganization(20)
        val org2 = createOrganization(21)
        organizationRepository.save(org1)
        organizationRepository.save(org2)
        val orgs = userGroupService.getAllOrganizations(authUsername = authUser.username)
        assertEquals(2, orgs.size)
    }

    @Test
    fun shouldGetAllGroupsForAdmin() {
        val org = createOrganization(30)
        val g1 = createGroup(30, org)
        val g2 = createGroup(31, org)
        organizationRepository.save(org)
        groupRepository.save(g1)
        groupRepository.save(g2)
        val groups = userGroupService.getAllGroups(authUsername = authUser.username)
        assertEquals(2, groups.size)
    }

    @Test
    fun shouldGetAllUsersInGroup() {
        val org = createOrganization(40)
        val group = createGroup(40, org)
        val user1 = testUtilsService.createUnsavedTestUser().copy(username = "member1")
        val user2 = testUtilsService.createUnsavedTestUser().copy(username = "member2")
        testUtilsService.saveUser(user1)
        testUtilsService.saveUser(user2)
        group.members.add(user1)
        group.members.add(user2)
        org.groups.add(group)
        organizationRepository.save(org)
        groupRepository.save(group)
        val users = userGroupService.getAllUsersInGroup(group.id!!,0,100)
        assertEquals(2, users.size)
    }

    @Test
    fun shouldGetAllUsersToSeeAsAdmin() {
        testUtilsService.userRepository.saveAll(listOf(
            UserEntity(firstName = "firstName", lastName = "lastName", username = "user1", roles= mutableSetOf(Role(RoleName.STUDENT))),
            UserEntity(firstName = "firstName", lastName = "lastName", username = "user2", roles= mutableSetOf(Role(RoleName.STUDENT))),
            UserEntity(firstName = "firstName", lastName = "lastName", username = "user3", roles= mutableSetOf(Role(RoleName.STUDENT))
        )))
        val users = userGroupService.getAllUsersToSee(authUser.username)
        assertEquals(4, users.size)
    }

    @Test
    fun shouldGetAllGroupsForNonAdmin() {
        // create a non-admin user and assign to a group
        val user = testUtilsService.createUnsavedTestUser().copy(username = "plain_member")
        testUtilsService.saveUser(user)
        val org = createOrganization(50)
        val group = createGroup(50, org)
        group.members.add(user)
        org.groups.add(group)
        organizationRepository.save(org)
        groupRepository.save(group)

        val groups = userGroupService.getAllGroups(authUsername = user.username)
        // user groups only (not organizations) - here user is member of one group
        assertEquals(1, groups.size)
    }

    @Test
    fun shouldGetAllOrganizationsForNonAdmin() {
        val user = testUtilsService.createUnsavedTestUser().copy(username = "org_member")
        testUtilsService.saveUser(user)
        val org = createOrganization(60)
        org.members.add(user)
        organizationRepository.save(org)

        val orgs = userGroupService.getAllOrganizations(authUsername = user.username)
        assertEquals(1, orgs.size)
    }

    @Test
    fun addAdminUserToGroup_throwsWhenUserNotFound() {
        val org = createOrganization(70)
        val group = createGroup(70, org)
        organizationRepository.save(org)
        groupRepository.save(group)
        assertThrows<NoSuchElementException> {
            userGroupService.addAdminUserToGroup("non_existing_user", group.id!!)
        }
    }

    @Test
    fun addAdminUserToGroup_throwsWhenGroupNotFound() {
        val user = testUtilsService.createUnsavedTestUser().copy(username = "exists_user")
        testUtilsService.saveUser(user)
        assertThrows<NoSuchElementException> {
            userGroupService.addAdminUserToGroup(user.username, 99999)
        }
    }

    @Test
    @Transactional
    fun addAdminUserToGroup_idempotentWhenCalledTwice() {
        val org = createOrganization(80)
        val group = createGroup(80, org)
        val user = testUtilsService.createUnsavedTestUser().copy(username = "dup_admin")
        testUtilsService.saveUser(user)
        organizationRepository.save(org)
        groupRepository.save(group)
        userGroupService.addAdminUserToGroup(user.username, group.id!!)
        userGroupService.addAdminUserToGroup(user.username, group.id!!)
        val groupInDb = groupRepository.findById(group.id!!).get()
        // admin should only be present once
        assertEquals(1, groupInDb.admins.size)
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