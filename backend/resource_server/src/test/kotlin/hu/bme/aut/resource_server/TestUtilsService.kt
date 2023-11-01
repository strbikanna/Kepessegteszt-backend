package hu.bme.aut.resource_server

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.ability.AbilityRepository
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.gameplayresult.GameplayResultEntity
import hu.bme.aut.resource_server.gameplayresult.GameplayResultRepository
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_snapshot.EnumProfileSnapshotRepository
import hu.bme.aut.resource_server.profile_snapshot.FloatProfileSnapshotRepository
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
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
    @Autowired  var enumProfileSnapshotRepository: EnumProfileSnapshotRepository,
    @Autowired  var gameRepository: GameRepository,
    @Autowired  var gameplayResultRepository: GameplayResultRepository,
    @Autowired  var recommendedGameRepository: RecommendedGameRepository,
) {
    val authHeaderName = "authUser"
    val gameAuthHeaderName = "authGame"
    var authUsername = "authenticated-test-user"
    var authGameId = 1
    val abilityGf = AbilityEntity(code = "Gf", name="Fluid intelligence", description = "Ability to discover the underlying characteristic that governs a problem or a set of materials." )
    val abilityGq = AbilityEntity(code = "Gq", name="Quantitative knowledge", description = "Range of general knowledge about mathematics." )
    val abilityGsm = AbilityEntity(code = "Gsm", name="Short term memory", description = "Ability to attend to and immediately recall temporally ordered elements in the correct order after a single presentation." )
    val abilityGv = AbilityEntity("Gv", "Visual processing", "?", )
    val abilityColorsense = AbilityEntity("Cls", "Color sense", "If the brain/eye is capable to differentiate colors", AbilityType.ENUMERATED)
    fun emptyRepositories(){
        floatProfileSnapshotRepository.deleteAll()
        enumProfileSnapshotRepository.deleteAll()
        gameplayResultRepository.deleteAll()
        recommendedGameRepository.deleteAll()
        userRepository.deleteAll()
        gameRepository.deleteAll()
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
    fun saveAuthUserWithRights(vararg roles: RoleName): UserEntity{
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

    fun saveAuthGame(): GameEntity{
        val game = GameEntity(
            name = "Auth game",
            icon = "auth icon",
            thumbnailPath = "test/files/assets",
            description = "Auth test game description",
            active = true,
            url = "testUrl",
            configDescription = mutableMapOf()
        )
        val entity = gameRepository.save(game)
        authGameId = entity.id!!
        return entity
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

    fun createAndSaveGame(): GameEntity{
        val json = mutableMapOf<String, Any>("ability" to "Gf")
        val game = GameEntity(
            name = "Test game",
            icon= "test",
            thumbnailPath = "test/files/assets",
            description = "Test game description",
            active = true,
            url = "testUrl",
            configDescription = json.toMutableMap()
        )
        return gameRepository.save(game)
    }

    fun createAndSaveRecommendedGame(user: UserEntity): RecommendedGameEntity{
        val game = createAndSaveGame()
        val recommendedGameEntity = RecommendedGameEntity(
            game = game,
            recommendedTo = user,
            config = mapOf(),
        )
        return recommendedGameRepository.save(recommendedGameEntity)
    }

    fun createGamePlayResult(): GameplayResultEntity{
        val json = mutableMapOf<String, Any?>()
        json["time"] = 100
        json["correct"] = 10
        json["all"] = 10
        json["level"] = 2
        val user = createUnsavedTestUser()
        userRepository.save(user)
        return GameplayResultEntity(
            result = json.toMap(),
            config = mutableMapOf<String, Any>(),
            user = user,
            recommendedGame = createAndSaveRecommendedGame(user)
        )
    }
}