package hu.bme.aut.auth_server.user

import org.springframework.data.repository.PagingAndSortingRepository

interface PagingUserRepository : PagingAndSortingRepository<UserEntity, Int> {
}