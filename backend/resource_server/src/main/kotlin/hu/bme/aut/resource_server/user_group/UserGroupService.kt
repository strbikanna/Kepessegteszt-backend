package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.user_group.organization.Organization
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserGroupService(
    @Autowired private var userGroupRepository: UserGroupRepository,
    @Autowired private var userRepository: UserRepository,
    @Autowired private var groupRepository: GroupRepository,
    @Autowired private var organizationRepository: OrganizationRepository

) {
    /**
     * Returns all user groups.
     * Should be called within transaction. (@Transactional)
     */
    fun getAllUserGroups(): List<UserGroup> {
        return userGroupRepository.findAll()
    }

    @Transactional
    fun addAdminUserToGroup(username: String, groupId: Int) {
        val user = userRepository.findByUsername(username).orElseThrow()
        val group = userGroupRepository.findById(groupId).orElseThrow()
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        dbGroup.admins.add(user)
        userGroupRepository.save(group)
    }

    /**
     * Removes user from group members and group admins
     * Also removes user from all child groups
     * @param user user to remove, must have id
     * @param group group to remove user from, must have id
     */
    @Transactional
    fun removeUserFromGroup(username: String, groupId: Int) {
        val user = userRepository.findByUsername(username).orElseThrow()
        val group = userGroupRepository.findById(groupId).orElseThrow()
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        dbGroup.members.remove(user)
        dbGroup.admins.remove(user)
        userGroupRepository.save(group)
        val allGroups = dbGroup.getAllGroups()
        allGroups.forEach {
            it.members.remove(user)
            it.admins.remove(user)
            userGroupRepository.save(it)
        }
    }

    /**
     * Removes user from group admins
     * Does not change the members of the group
     * @param user user to remove, must have id
     * @param group group to remove user from, must have id
     */
    @Transactional
    fun removeAdminFromGroup(username: String, groupId: Int) {
        val user = userRepository.findByUsername(username).orElseThrow()
        val group = userGroupRepository.findById(groupId).orElseThrow()
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        dbGroup.admins.remove(user)
        userGroupRepository.save(group)
    }

    @Transactional
    fun getAllUsersInGroup(groupId: Int): List<UserEntity> {
        val group = userGroupRepository.findById(groupId).orElseThrow()
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        val userIds = dbGroup.getAllUserIds()
        return userRepository.findByIdIn(userIds.toList())
    }

    @Transactional
    fun getAdminsOfGroup(groupId: Int): List<UserEntity> {
        val group = userGroupRepository.findById(groupId).orElseThrow()
        return group.admins.toList()
    }

    @Transactional
    fun createGroup(name: String, orgId: Int): Group {
        val orgOfGroup = organizationRepository.findById(orgId).orElseThrow()
        val group = Group(name = name, organization = orgOfGroup)
        return groupRepository.save(group)
    }
    @Transactional
    fun createOrganization(name: String, address: Address): Organization {
        val org = Organization(name = name, address = address)
        return organizationRepository.save(org)
    }
}