package hu.bme.aut.resource_server.user_group.group

import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Int> {
}