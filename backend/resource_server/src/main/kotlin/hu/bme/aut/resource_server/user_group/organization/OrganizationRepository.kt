package hu.bme.aut.resource_server.user_group.organization

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OrganizationRepository : JpaRepository<Organization, Int>{

    fun findByNameLikeOrderByNameAsc(name: String): List<Organization>

    @Query("SELECT m FROM Organization o " +
            "INNER JOIN o.members m " +
            "WHERE o.id IN :groupIds AND " +
            "(m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR CONCAT(m.firstName, m.lastName) LIKE %:name%)")
    fun searchMembersByNameInGroup(groupIds: List<Int>, name: String): List<UserEntity>
}