package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.user.PlainUserDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class SnapshotServiceTest(
    @Autowired private var snapshotService: SnapshotService,
    @Autowired private var testUtilsService: TestUtilsService,
) {
    @BeforeEach
    fun emptyDb(){
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
    }

    @Test
    fun shouldSaveSnapshotOfUser(){
        var userEntity = testUtilsService.createUnsavedTestUser()
        userEntity = testUtilsService.saveUser(userEntity)
        val snapshot = snapshotService.saveSnapshotOfUser(userEntity)
        assertNotNull(snapshot.id)
        assertNotNull(snapshot.timestamp)
        assertEquals(userEntity, snapshot.user)
        assertEquals(userEntity.profile.size, snapshot.profile.size)
    }

    @Test
    fun shouldSaveAndFindSnapshot(){
        var userEntity = testUtilsService.createUnsavedTestUser()
        userEntity = testUtilsService.saveUser(userEntity)
        val snapshot = snapshotService.saveSnapshotOfUser(userEntity)
        val snapShotFound = snapshotService.getSnapshotsOfUser(PlainUserDto(userEntity))
        assertEquals(1, snapShotFound.size)
        assertEquals(snapshot.id, snapShotFound[0].id)
        assertEquals(snapshot.profile.size, snapShotFound[0].profile.size)
        assertTrue(snapShotFound[0].profile.any { it.ability.code == "Gf" && it.abilityValue == 10})
        assertTrue(snapShotFound[0].profile.any { it.ability.code == "Gq" && it.abilityValue == 4})
    }

}