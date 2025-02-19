package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.utils.Gender
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.*

interface UserRepository: JpaRepository<UserEntity, Int>, JpaSpecificationExecutor<UserEntity> {
    fun findByUsername(username: String): Optional<UserEntity>

    fun findAllByUsernameIn(username: List<String>): List<UserEntity>

    fun existsByUsername(username: String): Boolean

    fun findByIdIn(ids: List<Int>): List<UserEntity>

    @Query("SELECT u.id FROM UserEntity u")
    fun findAllIds(): List<Int>

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileFloat LEFT JOIN FETCH u.profileEnum WHERE u.id = :id")
    fun findByIdWithProfile(id: Int): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.profileFloat LEFT JOIN FETCH u.profileEnum WHERE u.username = :userName")
    fun findByUsernameWithProfile(userName: String): Optional<UserEntity>

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.roles WHERE u.username = :username")
    fun findByUsernameWithRoles(username: String): Optional<UserEntity>

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.firstName = :firstName, u.lastName = :lastName, " +
            "u.address = :address, u.birthDate = :birthDate, u.gender = :gender" +
            " WHERE u.id = :id")
    fun updateUserData(firstName: String, lastName: String, address: Address?, birthDate: LocalDate?, gender: Gender?, id: Int): Int

    @Query("SELECT SUM(p.abilityValue) FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND p.abilityValue is not null " +
            "AND u.id IN :userIds" )
    fun getSumOfAbilityValuesInUserGroup(abilityCode: String, userIds: List<Int>): Double?

    @Query("SELECT AVG(p.abilityValue) FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND p.abilityValue is not null " +
            "AND u.id IN :userIds"
    )
    fun getAverageOfAbilityValuesInUserGroup(abilityCode: String, userIds: List<Int>): Double?

    @Query("SELECT MAX(p.abilityValue) FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND u.id IN :userIds" )
    fun getMaxOfAbilityValuesInUserGroup(abilityCode: String, userIds: List<Int>): Double?

    @Query("SELECT MIN(p.abilityValue) FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND p.abilityValue is not null " +
            "AND u.id IN :userIds" )
    fun getMinOfAbilityValuesInUserGroup(abilityCode: String, userIds: List<Int>): Double?

    @Query("SELECT p.abilityValue FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND p.abilityValue is not null " +
            "AND u.id IN :userIds " +
            "ORDER BY p.abilityValue ASC" )
    fun getAbilityValuesInUserGroupAscending(abilityCode: String, userIds: List<Int>): List<Double>

    @Query("SELECT p.abilityValue FROM UserEntity u JOIN u.profileFloat p " +
            "WHERE p.ability.code = :abilityCode " +
            "AND p.abilityValue is not null")
    fun getAllAbilityValues(abilityCode: String): List<Double>

    @Query("SELECT u FROM UserEntity u WHERE u.firstName LIKE %:name% OR u.lastName LIKE %:name% OR CONCAT(u.firstName, u.lastName) LIKE %:name%")
    fun searchByName(name: String): List<UserEntity>

    fun deleteAllByUsername(username: String)
}