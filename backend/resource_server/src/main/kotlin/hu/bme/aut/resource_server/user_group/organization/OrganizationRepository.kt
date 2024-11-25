package hu.bme.aut.resource_server.user_group.organization

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface OrganizationRepository : CrudRepository<Organization, Int>{

    fun findByNameLikeOrderByNameAsc(name: String): List<Organization>

    @Query("SELECT m FROM Organization o " +
            "JOIN FETCH o.members m " +
            "WHERE o.id = :groupId AND " +
            "(m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR CONCAT(m.firstName, m.lastName) LIKE %:name%)")
    fun searchMembersByNameInGroup(groupId: Int, name: String): List<UserEntity>
}