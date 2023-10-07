package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.ability.Ability
import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user.role.RoleName
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class ProfileSnapshotRepositoryTest(
    @Autowired private var profileSnapshotRepository: ProfileSnapshotRepository,
    @Autowired private var userRepository: UserRepository,
    @Autowired private var abilityRepository: AbilityRepository,
    ) {
    private val abilityGv = Ability("Gv", "Visual processing", "?")

    @BeforeEach
    fun emptyRepo() {
        userRepository.deleteAll()
        abilityRepository.save(abilityGv)
    }

    @Test
    @Transactional
    fun shouldSaveProfileSnapshotWithRelations() {
        val user = UserEntity(
            firstName = "Test",
            lastName = "User",
            username = "test_user",
            roles = mutableSetOf(Role(roleName = RoleName.STUDENT)),
            profile = mutableSetOf()
        )
        userRepository.save(user)
        val profileSnapshot = ProfileSnapshot(
            user = user,
            profile = mutableSetOf(
                ProfileSnapshotItem(
                    ability = abilityGv,
                    abilityValue = 4
                )
            )
        )
        profileSnapshotRepository.save(profileSnapshot)
        assertEquals(1L, profileSnapshotRepository.count())
        assertNotNull(profileSnapshot.id)
        assertNotNull(profileSnapshot.timestamp)
        val savedSnapshot = profileSnapshotRepository.findById(profileSnapshot.id!!).get()
        assertNotNull(savedSnapshot.profile.first().id)
        assertEquals(1, savedSnapshot.profile.size)
        assertEquals("Gv", savedSnapshot.profile.first().ability.code)
        assertEquals(user.id, savedSnapshot.user.id)
    }
}