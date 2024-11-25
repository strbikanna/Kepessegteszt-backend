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
import org.springframework.data.domain.PageRequest
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
    fun getAllUserGroups(pageIndex: Int =0, pageSize: Int = 100): List<UserGroup> {
        return userGroupRepository.findAll(PageRequest.of(pageIndex, pageSize)).toList()
    }

    fun getAllOrganizations(pageIndex: Int =0, pageSize: Int = 100): List<Organization> {
        return organizationRepository.findAll(PageRequest.of(pageIndex, pageSize)).toList()
    }

    fun getAllGroups(pageIndex: Int =0, pageSize: Int = 100): List<Group> {
        return groupRepository.findAll(PageRequest.of(pageIndex, pageSize)).toList()
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
        group.admins.remove(user)
        userGroupRepository.save(group)
    }

    /**
     * Returns all users in the group or organization based on its id
     */
    @Transactional
    fun getAllUsersInGroup(groupId: Int): List<UserEntity> {
        val group = userGroupRepository.findById(groupId).orElseThrow()
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        val userIds = dbGroup.getAllUserIds()
        return userRepository.findByIdIn(userIds.toList())
    }

    /**
     * Returns all admins in the group or org based on its id
     */
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

    @Transactional
    fun searchOrganizationMembersByName(orgIds: List<Int>, name: String): List<UserEntity> {
        return organizationRepository.searchMembersByNameInGroup(orgIds, name)
    }

    @Transactional
    fun searchGroupMembersByName(groupIds: List<Int>, name: String): List<UserEntity> {
        return groupRepository.searchMembersByNameInGroup(groupIds, name)
    }

    @Transactional
    fun searchGroupsByName(name: String): List<Group> {
        return groupRepository.findByNameLikeOrderByNameAsc(name)
    }

    @Transactional
    fun searchOrganizationsByName(name: String): List<Organization> {
        return organizationRepository.findByNameLikeOrderByNameAsc(name)
    }

    fun getChildrenOfOrganization(organizationId: Int): List<Group> {
        return groupRepository.getAllByOrganization(organizationId)
    }

    @Transactional
    fun getChildrenOfGroup(groupId: Int): List<Group> {
        return groupRepository.findById(groupId).orElseThrow().childGroups
    }

}