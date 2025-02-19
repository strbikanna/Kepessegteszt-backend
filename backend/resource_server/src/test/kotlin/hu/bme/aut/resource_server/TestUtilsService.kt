package hu.bme.aut.resource_server

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationRepository
import hu.bme.aut.resource_server.profile_snapshot.EnumProfileSnapshotRepository
import hu.bme.aut.resource_server.profile_snapshot.FloatProfileSnapshotRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.result.ResultRepository
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user_group.UserGroupRepository
import hu.bme.aut.resource_server.user_group.group.GroupRepository
import hu.bme.aut.resource_server.user_group.organization.OrganizationRepository
import hu.bme.aut.resource_server.utils.AbilityType
import hu.bme.aut.resource_server.utils.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TestUtilsService(
    @Autowired var userRepository: UserRepository,
    @Autowired var abilityRepository: AbilityRepository,
    @Autowired var floatProfileSnapshotRepository: FloatProfileSnapshotRepository,
    @Autowired var enumProfileSnapshotRepository: EnumProfileSnapshotRepository,
    @Autowired var gameRepository: GameRepository,
    @Autowired var resultRepository: ResultRepository,
    @Autowired var recommendedGameRepository: RecommendedGameRepository,
    @Autowired var resultForCalcRepository: ResultForCalculationRepository,
    @Autowired var userGroupRepository: UserGroupRepository,
    @Autowired var groupRepository: GroupRepository,
    @Autowired var organizationRepository: OrganizationRepository
) {
    val authHeaderName = "authUser"
    val gameAuthHeaderName = "authGame"
    var authUsername = "authenticated-test-user"
    var authGameId = 1
    val abilityGf = AbilityEntity(code = "Gf", name = "Fluid intelligence", description = "Ability to discover the underlying characteristic that governs a problem or a set of materials.")
    val abilityGq = AbilityEntity(code = "Gq", name = "Quantitative knowledge", description = "Range of general knowledge about mathematics.")
    val abilityGsm = AbilityEntity(code = "Gsm", name = "Short term memory", description = "Ability to attend to and immediately recall temporally ordered elements in the correct order after a single presentation.")
    val abilityGv = AbilityEntity("Gv", "Visual processing", "?")
    val abilityColorsense = AbilityEntity("Cls", "Color sense", "If the brain/eye is capable to differentiate colors", AbilityType.ENUMERATED)

    fun emptyRepositories() {
        resultForCalcRepository.deleteAll()
        floatProfileSnapshotRepository.deleteAll()
        enumProfileSnapshotRepository.deleteAll()
        resultRepository.deleteAll()
        recommendedGameRepository.deleteAll()
        userGroupRepository.deleteAll()
        userRepository.deleteAll()
        gameRepository.deleteAll()
    }

    fun fillAbilityRepository() {
        abilityRepository.deleteAll()
        abilityRepository.saveAll(listOf(abilityGf, abilityGq, abilityGsm, abilityGv, abilityColorsense))
    }

    fun createUnsavedTestUser(): UserEntity {
        val profile = mutableSetOf(
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

    fun saveAuthUserWithRights(vararg roles: RoleName): UserEntity {
        val user = UserEntity(
                username = authUsername,
                firstName = "Test",
                lastName = "User",
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
                roles = roles.map { Role(it) }.toMutableSet()
        )
        return userRepository.save(user)
    }

    fun saveAuthGame(): GameEntity {
        fillAbilityRepository()
        val abilities = mutableSetOf(abilityGf, abilityGq)
        val game = GameEntity(
                version = 1,
                name = "Auth game",
                thumbnailPath = "test/files/assets",
                description = "Auth test game description",
                active = true,
                configDescription = mutableMapOf(),
                affectedAbilities = abilities
        )
        val entity = gameRepository.save(game)
        authGameId = entity.id!!
        return entity
    }

    fun fillUserRepository() {
        fillAbilityRepository()
        val user1 = createUnsavedTestUser().copy(username = "test_user1")
        userRepository.save(user1)
        val profile = mutableSetOf(
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

    fun createAndSaveGame(): GameEntity {
        val game = GameEntity(
                version = 1,
                name = "Test game",
                thumbnailPath = "test/files/assets",
                description = "Test game description",
                active = true,
                configDescription = mutableMapOf("Level" to 0),
                affectedAbilities = mutableSetOf(abilityGf)
        )
        return gameRepository.save(game)
    }

    fun createAndSaveRecommendedGame(user: UserEntity): RecommendedGameEntity {
        val game = createAndSaveGame()
        val recommendedGameEntity = RecommendedGameEntity(
                game = game,
                recommendedTo = user,
                config = mapOf(),
        )
        return recommendedGameRepository.save(recommendedGameEntity)
    }

    fun createGamePlayResult(user: UserEntity): ResultEntity {
        val json = mutableMapOf<String, Any>()
        json["time"] = 100
        json["correct"] = 10
        json["all"] = 10
        json["level"] = 2
        json["passed"] = true
        return ResultEntity(
                result = json.toMap(),
                config = mutableMapOf(),
                user = user,
                recommendedGame = createAndSaveRecommendedGame(user)
        )
    }

    fun saveGame(game: GameEntity): GameEntity {
        return gameRepository.save(game)
    }

    fun saveUser(user: UserEntity): UserEntity {
        return userRepository.save(user)
    }

    fun saveUsers(users: List<UserEntity>) {
        userRepository.saveAll(users)
    }

    fun saveAbility(ability: AbilityEntity): AbilityEntity {
        return abilityRepository.save(ability)
    }

    fun saveResults(results: List<ResultForCalculationEntity>) {
        resultForCalcRepository.saveAll(results)
    }
}