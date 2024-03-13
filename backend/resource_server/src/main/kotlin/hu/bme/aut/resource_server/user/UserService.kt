package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
        @Autowired private var userRepository: UserRepository,
        @Autowired private var groupRepository: GroupRepository,
        @Autowired private var orgRepository: OrganizationRepository
){
    fun getAllUsers(): List<PlainUserDto>{
        return userRepository.findAll().map { PlainUserDto(it) }
    }
    fun getUserDtoByUsername(username: String): PlainUserDto {
        return PlainUserDto(userRepository.findByUsername(username).orElseThrow())
    }
    fun getUserDtoWithProfileByUsername(username: String): UserProfileDto {
        return UserProfileDto(userRepository.findByUsernameWithProfile(username).orElseThrow())
    }
    fun getUserEntityWithProfileByUsername(username: String): UserEntity {
        return userRepository.findByUsernameWithProfile(username).orElseThrow()
    }

    fun updateUser(user: PlainUserDto){
        val userEntity = userRepository.findByUsername(user.username).orElseThrow()
        userRepository.updateUserData(user.firstName, user.lastName, userEntity.id!!)
    }
    fun updateUserProfile(user: UserEntity): UserProfileDto {
        val userEntity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        userEntity.profileEnum = user.profileEnum
        userEntity.profileFloat = user.profileFloat
        val updatedEntity = userRepository.save(userEntity)
        return UserProfileDto(
            updatedEntity
        )
    }

    @Transactional
    fun getGroupsOfUser(username: String): List<UserGroupDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return user.groups.map { it.toDto() }
    }

    @Transactional
    fun addUserToGroup(username: String, groupId: Int){
        val user = userRepository.findByUsername(username).orElseThrow()
        val group = groupRepository.findById(groupId).orElseThrow()
        val orgOfGroup = group.organization
        if(!user.organizations.contains(orgOfGroup)){
            user.organizations.add(orgOfGroup)
            orgOfGroup.members.add(user)
            orgRepository.save(orgOfGroup)
        }
        group.members.add(user)
        groupRepository.save(group)
        user.groups.add(group)
        userRepository.save(user)
        user.groups.add(group)
        userRepository.save(user)
    }

    @Transactional
    fun addUserToOrganization(username: String, orgId: Int){
        val user = userRepository.findByUsername(username).orElseThrow()
        val org = orgRepository.findById(orgId).orElseThrow()
        user.organizations.add(org)
        org.members.add(user)
        orgRepository.save(org)
        userRepository.save(user)
    }

}