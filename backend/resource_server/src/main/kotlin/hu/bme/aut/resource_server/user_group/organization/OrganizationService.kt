package hu.bme.aut.resource_server.user_group.organization

import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrganizationService(
        @Autowired private var organizationRepository: OrganizationRepository,
        @Autowired private var groupRepository: GroupRepository
) {
    fun createOrganization(organization: Organization): Organization {
        return organizationRepository.save(organization)
    }

    @Transactional
    fun deleteOrganization(id: Int){
        val dbOrg = organizationRepository.findById(id).get()
        val childGroups = dbOrg.getAllGroups()
        groupRepository.deleteAll(childGroups)
        organizationRepository.delete(dbOrg)
    }
}