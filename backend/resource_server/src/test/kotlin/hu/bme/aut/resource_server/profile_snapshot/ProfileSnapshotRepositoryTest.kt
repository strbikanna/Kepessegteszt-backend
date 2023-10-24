package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import hu.bme.aut.resource_server.utils.RoleName
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
    @Autowired private var floatProfileSnapshotRepository: FloatProfileSnapshotRepository,
    @Autowired private var enumProfileSnapshotRepository: EnumProfileSnapshotRepository,
    @Autowired private var testUtilsService: TestUtilsService
) {


    @BeforeEach
    fun emptyRepo() {
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
    }

    @Test
    @Transactional
    fun shouldSaveProfileSnapshotWithRelations() {
        val user = UserEntity(
            firstName = "Test",
            lastName = "User",
            username = "test_user",
            roles = mutableSetOf(Role(roleName = RoleName.STUDENT)),
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
        )
        testUtilsService.saveUser(user)
        val profileSnapshotItem = FloatProfileSnapshotItem(
            user = user,
            abilityEntity = testUtilsService.abilityGv,
            abilityValue = 4.0

        )
        floatProfileSnapshotRepository.save(profileSnapshotItem)
        assertEquals(1L, floatProfileSnapshotRepository.count())
        assertNotNull(profileSnapshotItem.id)
        assertNotNull(profileSnapshotItem.timestamp)
        val savedSnapshot = floatProfileSnapshotRepository.findById(profileSnapshotItem.id!!).get()
        assertNotNull(savedSnapshot.id)
        assertEquals("Gv", savedSnapshot.abilityEntity.code)
        assertEquals(user.id, savedSnapshot.user.id)
    }

    @Test
    fun shouldSaveMultipleValuedSnapshot(){
        val user = testUtilsService.createUnsavedTestUser()
        testUtilsService.saveUser(user)
        val floatSnapshotItem = FloatProfileSnapshotItem(
            user = user,
            abilityEntity = testUtilsService.abilityGv,
            abilityValue = 4.0
        )
        val enumSnapshotItem = EnumProfileSnapshotItem(
            user = user,
            abilityEntity = testUtilsService.abilityColorsense,
            abilityValue = EnumAbilityValue.NO
        )
        floatProfileSnapshotRepository.save(floatSnapshotItem)
        enumProfileSnapshotRepository.save(enumSnapshotItem)
        val savedFloatsnapshot = floatProfileSnapshotRepository.findById(floatSnapshotItem.id!!).get()
        val savedEnumsnapshot = enumProfileSnapshotRepository.findById(enumSnapshotItem.id!!).get()
        assertEquals(EnumAbilityValue.NO, savedEnumsnapshot.abilityValue)
        assertEquals(4.0, savedFloatsnapshot.abilityValue)
        assertNotNull(savedFloatsnapshot.timestamp)
        assertNotNull(savedEnumsnapshot.timestamp)
    }
}