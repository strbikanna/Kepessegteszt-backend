package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity

object TestDataSource {
    val affectedAbility = AbilityEntity(code= "GAA", name="Short Term Auditive Memory", description="Ability to store auditive input and recall in short term")
    fun createGameForTest(): GameEntity{
        val config = mutableMapOf<String, Any>(
            "levelFieldName" to "level",
            "pointsFieldName" to "round",
            "maxPointsFieldName" to "maxRound",
            "extraPointsFieldName" to "healthPoints",
            "maxExtraPointsFieldName" to "maxHealthPoints",
            "maxRound" to 10,
            "maxLevel" to 10,
        )
      return GameEntity(
                version = 1,
                name = "testGame",
                description = "game1",
                thumbnailPath = "game1",
                active = true,
                url = null,
                configDescription = config,
                affectedAbilites = mutableSetOf(affectedAbility)
            )
    }
    fun createUsersForTestWithEmptyProfile(count: Int): List<UserEntity> {
        val userList = mutableListOf<UserEntity>()
        for(i in 1..count){
            userList.add(UserEntity(
                firstName = "user$i",
                lastName = "user$i",
                username = "user$i",
                profileFloat = mutableSetOf(),
                profileEnum = mutableSetOf(),
                roles = mutableSetOf(),
            ))
        }
        return userList
    }

    fun createNthUserWithAbilities(nth: Int, abilities: List<AbilityEntity>, abilityValues: List<Double>): UserEntity{
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
    fun createNormalizedResultForUser(user: UserEntity, resultValue: Double, game: GameEntity): ResultForCalculationEntity{
        return ResultForCalculationEntity(
            result = mutableMapOf(),
            normalizedResult = resultValue,
            config = mutableMapOf(),
            user = user,
            game = game,
        )

    }
    fun createResultsForCalculation_ThreePartition(users: List<UserEntity>, game: GameEntity): List<ResultForCalculationEntity>{
        val results = mutableListOf<ResultForCalculationEntity>()
        for(i in 1..users.size){
            results.add(
                ResultForCalculationEntity(
                result = mutableMapOf(
                    "level" to 2,
                    "round" to 8,
                    "maxRound" to 10,
                    "healthPoints" to 8,
                    "maxHealthPoints" to 10,
                ),
                config = mutableMapOf(),
                user = users[i-1],
                game = game,
            )
            )
        }
        for(i in users.size/6..users.size){
            results.add(
                ResultForCalculationEntity(
                result = mutableMapOf(
                    "level" to 3,
                    "round" to 6,
                    "maxRound" to 10,
                    "healthPoints" to 5,
                    "maxHealthPoints" to 10,
                ),
                config = mutableMapOf(),
                user = users[i-1],
                game = game,
            )
            )
        }
        for(i in 5*users.size/6..users.size){
            results.add(
                ResultForCalculationEntity(
                result = mutableMapOf(
                    "level" to 4,
                    "round" to 5,
                    "maxRound" to 10,
                    "healthPoints" to 2,
                    "maxHealthPoints" to 10,
                ),
                config = mutableMapOf(),
                user = users[i-1],
                game = game,
            )
            )
        }
        return results
    }
}