package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.TestUtilsService
import hu.bme.aut.resource_server.profile.EnumProfileItem
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.utils.EnumAbilityValue
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class ProfileSnapshotServiceTest(
    @Autowired private var snapshotService: ProfileSnapshotService,
    @Autowired private var testUtilsService: TestUtilsService,
) {
    @BeforeEach
    fun emptyDb(){
        testUtilsService.emptyRepositories()
        testUtilsService.fillAbilityRepository()
    }

    /*
    @Test
    fun shouldSaveSnapshotOfUser(){
        var userEntity = testUtilsService.createUnsavedTestUser()
        userEntity = testUtilsService.saveUser(userEntity)
        snapshotService.saveSnapshotOfUser(userEntity)
        val savedSnapshots = snapshotService.getSnapshotsOfUser(PlainUserDto(userEntity))
        assertEquals(2, savedSnapshots.size)
        assertEquals(userEntity.id, savedSnapshots[0].user.id)
        assertEquals(userEntity.id, savedSnapshots[1].user.id)
        assertTrue(savedSnapshots.any { it.ability.code == "Gf" && it.abilityValue == 10.0})
    }

     */

    @Test
    fun shouldNotChangeSavedSnapshot(){
        var userEntity = testUtilsService.createUnsavedTestUser()
        userEntity = testUtilsService.saveUser(userEntity)
        snapshotService.saveSnapshotOfUser(userEntity)
        var snapShotsFound = snapshotService.getSnapshotsOfUser(PlainUserDto(userEntity))
        assertEquals(2, snapShotsFound.size)

        userEntity.profileEnum.add(
            EnumProfileItem(ability=testUtilsService.abilityColorsense, abilityValue= EnumAbilityValue.INCLINED)
        )
        userEntity.profileFloat.find { it.ability.code == "Gf" }!!.abilityValue = 5.2
        testUtilsService.saveUser(userEntity)
        snapshotService.saveSnapshotOfUser(userEntity)

        snapShotsFound = snapshotService.getSnapshotsOfUser(PlainUserDto(userEntity))
        assertEquals(5, snapShotsFound.size)
        assertTrue(snapShotsFound.any { it.ability.code == "Gf" && it.abilityValue == 10.0})
        assertTrue(snapShotsFound.any { it.ability.code == "Gf" && it.abilityValue == 5.2})
        assertTrue(snapShotsFound.any { it.abilityValue == EnumAbilityValue.INCLINED})
    }

}