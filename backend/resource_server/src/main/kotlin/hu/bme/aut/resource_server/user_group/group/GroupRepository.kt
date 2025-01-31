package hu.bme.aut.resource_server.user_group.group

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GroupRepository : JpaRepository<Group, Int> {
    fun findByNameLikeOrderByNameAsc(name: String): List<Group>

    @Query(
        "SELECT m FROM Group g " +
                "INNER JOIN g.members m " +
                "WHERE g.id IN :groupIds AND " +
            "(m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR CONCAT(m.firstName, m.lastName) LIKE %:name%)")
    fun searchMembersByNameInGroup(groupIds: List<Int>, name: String): List<UserEntity>

    @Query(
        "SELECT m FROM Group g " +
                "INNER JOIN g.members m " +
                "WHERE (m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR CONCAT(m.firstName, m.lastName) LIKE %:name%)")
    fun searchMembersByName( name: String): List<UserEntity>


    @Query(
        nativeQuery = true,
        value = "SELECT * FROM user_group WHERE organization_id = :organizationId AND parent_group_id IS NULL"
    )
    fun getAllByOrganization(organizationId: Int): List<Group>
}