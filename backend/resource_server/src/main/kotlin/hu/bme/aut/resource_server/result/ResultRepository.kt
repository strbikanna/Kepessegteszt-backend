package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ResultRepository : JpaRepository<ResultEntity, Long> {
    fun findAllByUser(user: UserEntity): List<ResultEntity>
    fun findAllByUser(user: UserEntity, page: Pageable): Page<ResultEntity>
    fun findAllByUserIn(user: List<UserEntity>, page: Pageable): Page<ResultEntity>
    fun findAllByPassed(passed: Boolean, page: Pageable): Page<ResultEntity>
    fun findAllByRecommendedGameGameIdIn(gameIds: List<Int>, page: Pageable): Page<ResultEntity>
    fun findAllByUserInAndPassed(users: List<UserEntity>, passed: Boolean, page: Pageable): Page<ResultEntity>
    fun findAllByUserInAndRecommendedGameGameIdIn(
        users: List<UserEntity>,
        gameIds: List<Int>,
        page: Pageable,
    ): Page<ResultEntity>
    fun findAllByPassedAndRecommendedGameGameIdIn(
        passed: Boolean,
        gameIds: List<Int>,
        page: Pageable,
    ): Page<ResultEntity>
    fun findAllByUserAndTimestampBetween(
        user: UserEntity,
        start: LocalDateTime,
        end: LocalDateTime = LocalDateTime.now()
    ): List<ResultEntity>

    fun findAllByUserInAndPassedAndRecommendedGameGameIdIn(
        users: List<UserEntity>?,
        passed: Boolean?,
        gameIds: List<Int>?,
        page: Pageable,
    ): Page<ResultEntity>

    fun countByUser(user: UserEntity): Long
    fun countByUserInAndRecommendedGameGameIdInAndPassed(user: List<UserEntity>, gameIds: List<Int>, passed: Boolean): Long
    fun countByUserInAndRecommendedGameGameIdIn(user:  List<UserEntity>, gameIds: List<Int>): Long
    fun countByUserInAndPassed(user:  List<UserEntity>, passed: Boolean): Long
    fun countByRecommendedGameGameIdInAndPassed(gameIds: List<Int>, passed: Boolean): Long
    fun countByRecommendedGameGameIdIn(gameIds: List<Int>): Long
    fun countByPassed(passed: Boolean): Long
    fun countByUserIn(user: List<UserEntity>): Long
    override fun findAll(page: Pageable): Page<ResultEntity>

}