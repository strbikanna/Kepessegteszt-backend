package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.dto.ProfileItem
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import hu.bme.aut.resource_server.user.filter.UserSpecification
import hu.bme.aut.resource_server.user_group.UserGroupDto
import hu.bme.aut.resource_server.user_group.UserGroupRepository
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserGroupDataService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var groupRepository: GroupRepository,
    @Autowired private var orgRepository: OrganizationRepository,
    @Autowired private var uGroupRepository: UserGroupRepository,
) {
    @Transactional
    fun getGroupsOfUser(username: String): List<UserGroupDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val allGroups = mutableListOf(user.groups, user.organizations).flatten().map { it.toDto() }
        return allGroups
    }

    @Transactional
    fun getGroupById(groupId: Int): UserGroupDto {
        return groupRepository.findById(groupId).orElseThrow().toDto()
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

    fun getAllUserIdsByFilter(filter: UserFilterDto): List<Int> {
        return userRepository.findAll(UserSpecification(filter)).map { it.id!! }
    }

    fun getAllValuesOfAbility(abilityCode: String): List<Double> {
        return userRepository.getAllAbilityValues(abilityCode)
    }

    @Transactional
    fun getAbilityValuesInUserGroupAscending(groupId: Int, abilityCode: String): List<Double> {
        val group = uGroupRepository.findById(groupId).orElseThrow()
        val userIds = group.getAllUserIds().toList()
        return userRepository.getAbilityValuesInUserGroupAscending(abilityCode, userIds)
    }

    @Transactional
    fun getAbilityToAverageValueInGroup(groupId: Int?, userFilterDto: UserFilterDto?, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, userFilterDto, abilities, userRepository::getAverageOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToSumValueInGroup(groupId: Int?, userFilterDto: UserFilterDto?, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, userFilterDto, abilities, userRepository::getSumOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToMaxValueInGroup(groupId: Int?, userFilterDto: UserFilterDto?, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, userFilterDto, abilities, userRepository::getMaxOfAbilityValuesInUserGroup)
    }

    @Transactional
    fun getAbilityToMinValueInGroup(groupId: Int?, userFilterDto: UserFilterDto?, abilities: Set<AbilityEntity>): List<ProfileItem> {
        return getAbilityToAggregateValuesInGroup(groupId, userFilterDto, abilities, userRepository::getMinOfAbilityValuesInUserGroup)
    }

    private fun getAbilityToAggregateValuesInGroup(groupId: Int?,
                                                   userFilterDto: UserFilterDto?,
                                                   abilities: Set<AbilityEntity>,
                                                   aggregationSupplier: (abilityCode: String, userIds: List<Int>) -> Double?
    ): List<ProfileItem> {
        val userIds : MutableList<Int> = mutableListOf()
        if(groupId == null && userFilterDto == null){
            userIds.addAll(userRepository.findAll().map { it.id!! })
        }
        groupId?.let {
            userIds.addAll(getUserIdsInGroup(it))
        }
        userFilterDto?.let {
            if(userIds.isEmpty()){
                userIds.addAll(getAllUserIdsByFilter(it))
            }
            else {
                userIds.retainAll(getAllUserIdsByFilter(it))
            }
        }
        val abilityToAggregate = mutableMapOf<AbilityEntity, Double>()
        abilities.forEach {
            val aggregateValue = aggregationSupplier(it.code, userIds)
            if(aggregateValue != null){
                abilityToAggregate[it] = aggregateValue
            }
        }
        return abilityToAggregate.map { ProfileItem(it.key, it.value) }
    }

    private fun getUserIdsInGroup(groupId: Int): List<Int> {
        val group = uGroupRepository.findById(groupId).orElseThrow()
        return group.getAllUserIds().toList()
    }
}