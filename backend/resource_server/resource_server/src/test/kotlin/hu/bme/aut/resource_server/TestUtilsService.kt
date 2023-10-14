package hu.bme.aut.resource_server

import hu.bme.aut.resource_server.ability.Ability
import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_snapshot.EnumProfileSnapshotRepository
import hu.bme.aut.resource_server.profile_snapshot.FloatProfileSnapshotRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.utils.AbilityType
import hu.bme.aut.resource_server.utils.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestUtilsService(
    @Autowired  var userRepository: UserRepository,
    @Autowired  var abilityRepository: AbilityRepository,
    @Autowired  var floatProfileSnapshotRepository: FloatProfileSnapshotRepository,
    @Autowired  var enumProfileSnapshotRepository: EnumProfileSnapshotRepository
) {
    val authHeaderName = "authUser"
    var authUsername = "authenticated-test-user"
    val abilityGf = Ability(code = "Gf", name="Fluid intelligence", description = "Ability to discover the underlying characteristic that governs a problem or a set of materials." )
    val abilityGq = Ability(code = "Gq", name="Quantitative knowledge", description = "Range of general knowledge about mathematics." )
    val abilityGsm = Ability(code = "Gsm", name="Short term memory", description = "Ability to attend to and immediately recall temporally ordered elements in the correct order after a single presentation." )
    val abilityGv = Ability("Gv", "Visual processing", "?", )
    val abilityColorsense = Ability("Cls", "Color sense", "If the brain/eye is capable to differentiate colors", AbilityType.ENUMERATED)
    fun emptyRepositories(){
        floatProfileSnapshotRepository.deleteAll()
        enumProfileSnapshotRepository.deleteAll()
        userRepository.deleteAll()
    }

    fun fillAbilityRepository(){
        abilityRepository.deleteAll()
        abilityRepository.saveAll(listOf(abilityGf, abilityGq, abilityGsm, abilityGv, abilityColorsense))
    }
    fun createUnsavedTestUser(): UserEntity{
        val profile  = mutableSetOf(
            FloatProfileItem(
                ability = abilityGf,
                abilityValue = 10.0
            ),
            FloatProfileItem(
                ability = abilityGq,
                abilityValue = 4.0
            ),
        )
        return UserEntity(
                username = "test_user",
                firstName = "Test",
                lastName = "User",
                profileFloat = profile,
                profileEnum = mutableSetOf(),
                roles = mutableSetOf(Role(RoleName.STUDENT))
            )
    }
    fun saveAuthUserWithRights(vararg roles: RoleName){
        val user = UserEntity(
            username = authUsername,
            firstName = "Test",
            lastName = "User",
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
            roles = roles.map { Role(it) }.toMutableSet()
        )
        userRepository.save(user)
    }
    fun saveUser(user: UserEntity): UserEntity{
        return userRepository.save(user)
    }

    fun fillUserRepository(){
        userRepository.deleteAll()
        fillAbilityRepository()
        val user1 = createUnsavedTestUser().copy(username = "test_user1")
        userRepository.save(user1)
        val profile  = mutableSetOf(
            FloatProfileItem(
                ability = abilityGsm,
                abilityValue = 2.0
            ),
            FloatProfileItem(
                ability = abilityGq,
                abilityValue = 7.0
            ),
        )
        val user2 = UserEntity(
            username = "test_user2",
            firstName = "Test",
            lastName = "User",
            profileFloat = profile,
            profileEnum = mutableSetOf(),
            roles = mutableSetOf(Role(RoleName.STUDENT))
        )
        userRepository.save(user2)
    }
}