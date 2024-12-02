package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.game_config.ConfigItem
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.result.ResultEntity
import hu.bme.aut.resource_server.user.UserEntity

object TestDataSource {
    val affectedAbility = AbilityEntity(
        code = "GAA",
        name = "Short Term Auditive Memory",
        description = "Ability to store auditive input and recall in short term"
    )

    fun createGameForTest(): GameEntity {
        val configItem1 = ConfigItem(
            paramName = "timeLimit",
            initialValue = 10000,
            easiestValue = 20000,
            hardestValue = 5000,
            increment = -1000,
            paramOrder = 2,
            description = "Time limit for each round",
        )
        val configItem2 = ConfigItem(
            paramName = "speed",
            initialValue = 10,
            easiestValue = 5,
            hardestValue = 15,
            increment = 1,
            paramOrder = 1,
            description = "Speed",
        )
        return GameEntity(
            id = 1,
            version = 1,
            name = "Meteorháború",
            description = "game1",
            thumbnailPath = "game1",
            active = true,
            affectedAbilities = mutableSetOf(affectedAbility),
            configItems = mutableSetOf(configItem1, configItem2),
        )
    }

    fun createUsersForTestWithEmptyProfile(count: Int): List<UserEntity> {
        val userList = mutableListOf<UserEntity>()
        for (i in 1..count) {
            userList.add(
                UserEntity(
                    firstName = "user$i",
                    lastName = "user$i",
                    username = "user$i",
                    profileFloat = mutableSetOf(),
                    profileEnum = mutableSetOf(),
                    roles = mutableSetOf(),
                )
            )
        }
        return userList
    }

    fun createNthUserWithAbilities(nth: Int, abilities: List<AbilityEntity>, abilityValues: List<Double>): UserEntity {
        val user = UserEntity(
            firstName = "user$nth",
            lastName = "user$nth",
            username = "user$nth",
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
            roles = mutableSetOf(),
        )
        abilities.forEachIndexed { index, ability ->
            user.profileFloat.add(
                FloatProfileItem(
                    ability = ability,
                    abilityValue = abilityValues[index],
                )
            )
        }
        return user
    }

    fun createNormalizedResultForUser(
        user: UserEntity,
        resultValue: Double,
        game: GameEntity
    ): ResultForCalculationEntity {
        return ResultForCalculationEntity(
            result = mutableMapOf(),
            normalizedResult = resultValue,
            config = mutableMapOf(),
            user = user,
            game = game,
        )

    }

    fun createGameplayResultForUser(user: UserEntity, recommendation: RecommendedGameEntity): ResultEntity {
        return ResultEntity(
            id = 1,
            result = mutableMapOf(),
            config = recommendation.config.toMutableMap(),
            user = user,
            recommendedGame = recommendation,
        )
    }

    fun createRecommendationForUser(user: UserEntity, game: GameEntity): RecommendedGameEntity {
        val config = mutableMapOf<String, Any>()
        game.configItems.forEach { configItem ->
            config[configItem.paramName] = configItem.initialValue
        }
        return RecommendedGameEntity(
            id = 1,
            config = config,
            recommendedTo = user,
            game = game,
        )
    }

    fun createResultsForCalculation_ThreePartition(
        users: List<UserEntity>,
        game: GameEntity
    ): List<ResultForCalculationEntity> {
        val results = mutableListOf<ResultForCalculationEntity>()
        for (i in 1..users.size) {
            results.add(
                ResultForCalculationEntity(
                    result = mutableMapOf(
                        "passed" to false
                    ),
                    config = mutableMapOf(),
                    user = users[i - 1],
                    game = game,
                )
            )
        }
        for (i in users.size / 2..users.size) {
            results.add(
                ResultForCalculationEntity(
                    result = mutableMapOf(
                        "passed" to true
                    ),
                    config = game.configItems.associate { it.paramName to it.initialValue }.toMutableMap(),
                    user = users[i - 1],
                    game = game,
                )
            )
        }
        for (i in users.size / 6..users.size) {
            results.add(
                ResultForCalculationEntity(
                    result = mutableMapOf(
                        "passed" to true
                    ),
                    config = game.configItems.associate { it.paramName to it.hardestValue }.toMutableMap(),
                    user = users[i - 1],
                    game = game,
                )
            )
        }
        return results
    }
}