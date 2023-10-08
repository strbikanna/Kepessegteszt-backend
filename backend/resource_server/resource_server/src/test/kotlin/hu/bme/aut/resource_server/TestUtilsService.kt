package hu.bme.aut.resource_server

import hu.bme.aut.resource_server.ability.Ability
import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user.role.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestUtilsService(
    @Autowired  var userRepository: UserRepository,
    @Autowired  var abilityRepository: AbilityRepository,
    @Autowired  var profileSnapshotRepository: ProfileSnapshotRepository
) {
    val abilityGf = Ability(code = "Gf", name="Fluid intelligence", description = "Ability to discover the underlying characteristic that governs a problem or a set of materials." )
    val abilityGq = Ability(code = "Gq", name="Quantitative knowledge", description = "Range of general knowledge about mathematics." )
    val abilityGsm = Ability(code = "Gsm", name="Short term memory", description = "Ability to attend to and immediately recall temporally ordered elements in the correct order after a single presentation." )

    fun emptyRepositories(){
        profileSnapshotRepository.deleteAll()
        userRepository.deleteAll()
        abilityRepository.deleteAll()
    }
    fun fillAbilityRepository(){
        abilityRepository.deleteAll()
        abilityRepository.saveAll(listOf(abilityGf, abilityGq, abilityGsm))
    }
    fun createUnsavedTestUser(): UserEntity{
        val profile  = mutableSetOf(
            ProfileItem(
                ability = abilityGf,
                abilityValue = 10
            ),
            ProfileItem(
                ability = abilityGq,
                abilityValue = 4
            ),
        )
        return UserEntity(
                username = "test_user",
                firstName = "Test",
                lastName = "User",
                profile = profile,
                roles = mutableSetOf(Role(RoleName.STUDENT))
            )
    }

    fun fillUserRepository(){
        userRepository.deleteAll()
        fillAbilityRepository()
        val user1 = createUnsavedTestUser().copy(username = "test_user1")
        userRepository.save(user1)
        val profile  = mutableSetOf(
            ProfileItem(
                ability = abilityGsm,
                abilityValue = 2
            ),
            ProfileItem(
                ability = abilityGq,
                abilityValue = 7
            ),
        )
        val user2 = UserEntity(
            username = "test_user2",
            firstName = "Test",
            lastName = "User",
            profile = profile,
            roles = mutableSetOf(Role(RoleName.STUDENT))
        )
        userRepository.save(user2)
    }
}