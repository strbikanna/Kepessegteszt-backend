package hu.bme.aut.resource_server.user_group.organization

import org.springframework.data.repository.CrudRepository

interface OrganizationRepository : CrudRepository<Organization, Int>{
}