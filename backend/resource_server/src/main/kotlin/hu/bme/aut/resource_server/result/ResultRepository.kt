package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.LocalDateTime

interface ResultRepository: CrudRepository<ResultEntity, Long>, PagingAndSortingRepository<ResultEntity, Long>{
    fun findAllByUser(user: UserEntity): List<ResultEntity>
    fun findAllByUser(user: UserEntity, page: Pageable): Page<ResultEntity>
    fun findAllByUserIn(user: List<UserEntity>, page: Pageable): Page<ResultEntity>
    override fun findAll(page: Pageable): Page<ResultEntity>
    fun findAllByUserAndTimestampBetween(user: UserEntity, start: LocalDateTime, end: LocalDateTime = LocalDateTime.now()): List<ResultEntity>

    fun countByUser(user: UserEntity): Long
}