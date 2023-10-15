package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import hu.bme.aut.resource_server.utils.RoleName
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class UserRepositoryTest(
        @Autowired private var userRepository: UserRepository,
        @Autowired private var testService: TestUtilsService,
) {
    @BeforeEach
    fun emptyRepo() {
        testService.emptyRepositories()
        testService.fillAbilityRepository()
    }

    @Transactional
    @Test
    fun shouldSaveUser() {
        val profile = mutableSetOf(
                FloatProfileItem(
                        ability = testService.abilityGv,
                        abilityValue = 10.0
                ),
                FloatProfileItem(
                        ability = testService.abilityGsm,
                        abilityValue = 4.0
                ),
        )
        val user = UserEntity(
                firstName = "Test", lastName = "User", username = "test_user",
                profileFloat = profile, profileEnum = mutableSetOf(),
                roles = mutableSetOf(Role(roleName = RoleName.PARENT))
        )
        userRepository.save(user)
        assertNotNull(user.id)
        val savedUser = userRepository.findById(user.id!!).get()
        assertNotNull(savedUser.id)
        assertNotNull(savedUser.profileFloat.first().id)
        assertEquals(2, savedUser.profileFloat.size)
    }

    @Test
    fun shouldSaveMoreUsersCorrectly() {
        val user1 = saveAndTestUser1()
        val user2 = saveAndTestUser2()

        testSavedUsers(user1, user2)
    }

    @Transactional
    private fun testSavedUsers(
            user1: UserEntity,
            user2: UserEntity
    ) {
        val savedUser1 = userRepository.findByIdWithProfile(user1.id!!).get()
        val savedUser2 = userRepository.findByIdWithProfile(user2.id!!).get()
        assertEquals(2, savedUser1.profileFloat.size)
        assertEquals(1, savedUser1.profileEnum.size)
        assertEquals(1, savedUser2.profileFloat.size)
        assertTrue(savedUser1.profileFloat.any { it.ability.code == "Gv" })
        assertTrue(savedUser1.profileEnum.any { it.ability.code == "Gv" })
        assertTrue(savedUser1.profileFloat.any { it.ability.code == "Gsm" })
        assertTrue(savedUser2.profileFloat.any { it.ability.code == "Gsm" })
    }

    @Transactional
    fun saveAndTestUser1(): UserEntity {
        val profileFloat = mutableSetOf(
                FloatProfileItem(
                        ability = testService.abilityGv,
                        abilityValue = 10.0
                ),
                FloatProfileItem(
                        ability = testService.abilityGsm,
                        abilityValue = 4.0
                ),
        )
        val profileEnum = mutableSetOf(
                EnumProfileItem(
                        ability = testService.abilityGv,
                        abilityValue = EnumAbilityValue.YES
                ),
        )
        //user with Gv and Gsm
        val user1 = UserEntity(
                firstName = "Test", lastName = "User", username = "test_user1",
                profileFloat = profileFloat, profileEnum = profileEnum,
                roles = mutableSetOf(Role(roleName = RoleName.STUDENT))
        )

        userRepository.save(user1)
        assertNotNull(user1.id)
        assertNotNull(user1.profileFloat.first().id)
        return user1
    }

    @Transactional
    fun saveAndTestUser2(): UserEntity {
        val profile2 = mutableSetOf(
                FloatProfileItem(
                        ability = testService.abilityGsm,
                        abilityValue = 4.0
                ),
        )
        //user with Gsm
        val user2 = UserEntity(
                firstName = "Test", lastName = "User", username = "test_user2",
                profileFloat = profile2, profileEnum = mutableSetOf(),
                roles = mutableSetOf(Role(roleName = RoleName.STUDENT))
        )
        userRepository.save(user2)
        assertEquals(2, userRepository.count())
        assertNotNull(user2.id)
        return user2
    }
}