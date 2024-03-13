package hu.bme.aut.resource_server.user_group

import org.springframework.data.jpa.repository.JpaRepository

interface UserGroupRepository : JpaRepository<UserGroup, Int>{
}