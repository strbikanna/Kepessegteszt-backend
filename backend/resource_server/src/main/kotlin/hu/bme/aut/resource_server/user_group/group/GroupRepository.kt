package hu.bme.aut.resource_server.user_group.group

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface GroupRepository : JpaRepository<Group, Int> {
    fun findByNameLikeOrderByNameAsc(name: String): List<Group>

    @Query(
        "SELECT m FROM Group g " +
                "JOIN FETCH g.members m " +
                "WHERE g.id = :groupId AND " +
            "(m.firstName LIKE %:name% OR m.lastName LIKE %:name% OR CONCAT(m.firstName, m.lastName) LIKE %:name%)")
    fun searchMembersByNameInGroup(groupId: Int, name: String): List<UserEntity>
}