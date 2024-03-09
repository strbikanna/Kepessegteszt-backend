package hu.bme.aut.resource_server.user_group

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserGroupService(
        @Autowired private var userGroupRepository: UserGroupRepository,
        @Autowired private var userRepository: UserRepository,
) {
    /**
     * Returns all user groups.
     * Should be called within transaction. (@Transactional)
     */
    fun getAllUserGroups(): List<UserGroup> {
        return userGroupRepository.findAll()
    }

    /**
     * Removes user from group members and group admins
     * Also removes user from all child groups
     * @param user user to remove, must have id
     * @param group group to remove user from, must have id
     */
    @Transactional
    fun removeUserFromGroup(user: UserEntity, group: UserGroup) {
        if(user.id == null || group.id == null) {
            throw IllegalArgumentException("User and group must have an id.")
        }
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
    fun removeAdminFromGroup(user: UserEntity, group: UserGroup) {
        if(user.id == null || group.id == null) {
            throw IllegalArgumentException("User and group must have an id.")
        }
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        dbGroup.admins.remove(user)
        userGroupRepository.save(group)
    }
    @Transactional
    fun getAllUsersInGroup(group: UserGroup): List<UserEntity> {
        val dbGroup = userGroupRepository.findById(group.id!!).get()
        val userIds = dbGroup.getAllUserIds()
        return userRepository.findByIdIn(userIds.toList())
    }
}