package hu.bme.aut.resource_server.profile.description_text

import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import hu.bme.aut.resource_server.llm.abilities2text.AbiltityToTextDto
import hu.bme.aut.resource_server.user.UserGroupDataService
import hu.bme.aut.resource_server.user.UserService
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProfileDescriptionService(
    private val repository: ProfileDescriptionTextRepository,
    private val abilitiesToTextService: AbilitiesToTextService,
    private val userService: UserService,
    val userGroupService: UserGroupDataService,

    ) {

    suspend fun getProfileDescriptionOfUser(username: String, prompt: String = ""): ProfileDescriptionTextDto =
        withContext(Dispatchers.IO) {
            val dbEntity = repository.findByUserUsername(username)
            if (dbEntity != null && !isOlderThanOneWeek(dbEntity.timestamp)) {
                return@withContext ProfileDescriptionTextDto(dbEntity)
            }
            if (dbEntity != null) {
                deleteProfileDescriptionOfUser(username)
            }
            val user = userService.getUserEntityWithProfileByUsername(username)
            val generatedText = generateDescriptionText(username, prompt)
            val newEntity = ProfileDescriptionTextEntity(
                generatedText = generatedText.abilitiesAsText,
                user = user
            )
            repository.save(newEntity)
            return@withContext ProfileDescriptionTextDto(newEntity)
        }

    fun deleteProfileDescriptionOfUser(username: String) {
        repository.deleteByUserUsername(username)
    }

    suspend fun generateComparisonTextToGroup(
        username: String,
        userGroupId: Int?,
        prompt: String,
        userFilter: UserFilterDto?
    ): AbiltityToTextDto {
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()

        val user = userService.getUserEntityWithProfileByUsername(username)
        val abilities = user.profileFloat.map { it.ability }.toSet()
        if (abilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        val groupAbilities = withContext(Dispatchers.IO) {
            userGroupService.getAbilityToAverageValueInGroup(userGroupId, userFilter, abilities)
        }
        if (groupAbilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        val groupName = userGroupId?.let { userGroupService.getGroupById(it).name } ?: "csoport"

        return abilitiesToTextService.generateFromAbilitiesComparedToGroup(
            userAbilities,
            groupAbilities,
            groupName,
            prompt
        )

    }

    private suspend fun generateDescriptionText(username: String, prompt: String): AbiltityToTextDto {
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()
        if (userAbilities.isEmpty()) {
            return AbiltityToTextDto("", "")
        }
        return abilitiesToTextService.generateFromAbilities(userAbilities, prompt)
    }

    private fun isOlderThanOneWeek(timestamp: LocalDateTime?): Boolean {
        if (timestamp == null) return false
        val oneWeekAgo = LocalDateTime.now().minusWeeks(1)
        return timestamp.isBefore(oneWeekAgo)
    }
}