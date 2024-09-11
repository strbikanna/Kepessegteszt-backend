package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.UserGroupRepository
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
        @Autowired private var userRepository: UserRepository,
        @Autowired private var groupRepository: GroupRepository,
        @Autowired private var orgRepository: OrganizationRepository,
        @Autowired private var uGroupRepository: UserGroupRepository,
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
        val allGroups = mutableListOf(user.groups, user.organizations).flatten().map { it.toDto() }
        return allGroups
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

    @Transactional
    fun getAbilityValuesInUserGroupAscending(groupId: Int, abilityCode: String): List<Double> {
        val group = uGroupRepository.findById(groupId).orElseThrow()
        val userIds = group.getAllUserIds().toList()
        return userRepository.getAbilityValuesInUserGroupAscending(abilityCode, userIds)
    }

    @Transactional
    fun getAbilityToAverageValueInGroup(groupId: Int, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, abilities, userRepository::getAverageOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToSumValueInGroup(groupId: Int, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, abilities, userRepository::getSumOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToMaxValueInGroup(groupId: Int, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, abilities, userRepository::getMaxOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToMinValueInGroup(groupId: Int, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, abilities, userRepository::getMinOfAbilityValuesInUserGroup)
    }

    private fun getAbilityToAggregateValuesInGroup(groupId: Int,
                                           abilities: Set<AbilityEntity>,
                                           aggregationSupplier: (abilityCode: String, userIds: List<Int>) -> Double?
    ): List<ProfileItem> {
        val group = uGroupRepository.findById(groupId).orElseThrow()
        val userIds = group.getAllUserIds().toList()
        val abilityToAggregate = mutableMapOf<AbilityEntity, Double>()
        abilities.forEach {
            val aggregateValue = aggregationSupplier(it.code, userIds)
            if(aggregateValue != null){
                abilityToAggregate[it] = aggregateValue
            }
        }
        return abilityToAggregate.map { convertToProfileItem(it.key, it.value)}
    }

    private fun convertToProfileItem(ability: AbilityEntity, value: Double): ProfileItem {
        return ProfileItem( ability, value)
    }

}