package hu.bme.aut.resource_server.user_group.group

import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GroupService(
        @Autowired private var groupRepository: GroupRepository,
        @Autowired private var organizationRepository: OrganizationRepository
) {
    @Transactional
    fun getAllGroupsInOrg(orgId: Int): List<Group> {
        val org = organizationRepository.findById(orgId).get()
        return org.getAllGroups()
    }

    fun createGroup(group: Group): Group {
        val org = group.organization
        org.groups.add(group)
        organizationRepository.save(org)
        return groupRepository.save(group)
    }

    @Transactional
    fun deleteGroup(id: Int){
        val dbGroup = groupRepository.findById(id).get()
        val org = dbGroup.organization
        val childGroups = dbGroup.getAllGroups()
        org.groups.remove(dbGroup)
        org.groups.removeAll(childGroups)
        groupRepository.deleteAll(childGroups)
        organizationRepository.save(org)
        groupRepository.delete(dbGroup)
    }
}