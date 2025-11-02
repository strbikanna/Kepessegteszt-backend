package hu.bme.aut.resource_server.profile.description_text

import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import hu.bme.aut.resource_server.llm.abilities2text.ChatMessageResponse
import hu.bme.aut.resource_server.user.UserGroupDataService
import hu.bme.aut.resource_server.user.UserService
import hu.bme.aut.resource_server.user.filter.UserFilterDto
import jakarta.transaction.Transactional
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

    private object MinAccuracy {
        const val VALUE = 0.5
    }

    @Transactional
    suspend fun getProfileDescriptionOfUser(username: String): ProfileDescriptionTextDto =
        withContext(Dispatchers.IO) {
            var dbEntity = repository.findAllByUserUsername(username).firstOrNull()
            if (dbEntity != null && !isOlderThanOneWeek(dbEntity.timestamp)) {
                return@withContext ProfileDescriptionTextDto(dbEntity)
            }
            if (dbEntity != null) {
                deleteProfileDescriptionOfUser(username)
            }
            val user = userService.getUserEntityWithProfileByUsername(username)
            val generatedText = generateDescriptionText(username, "")
            val newEntity = ProfileDescriptionTextEntity(
                generatedText = generatedText.response,
                user = user
            )
            repository.save(newEntity)
            return@withContext ProfileDescriptionTextDto(newEntity)
        }

    suspend fun generateProfileDescriptionOfUser(username: String, prompt: String = ""): ProfileDescriptionTextDto =
        withContext(Dispatchers.IO) {
            val generatedText = generateDescriptionText(username, prompt)
            return@withContext ProfileDescriptionTextDto(
                generatedText = generatedText.response,
                prompt = generatedText.prompt
            )
        }

    @Transactional
    fun deleteProfileDescriptionOfUser(username: String) {
        repository.deleteByUserUsername(username)
    }

    suspend fun generateComparisonTextToGroup(
        username: String,
        userGroupId: Int?,
        prompt: String,
        userFilter: UserFilterDto?
    ): ProfileDescriptionTextDto {
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()

        val user = userService.getUserEntityWithProfileByUsername(username)
        val abilities = user.profileFloat.map { it.ability }.toSet()
        if (abilities.isEmpty() || userAbilities.none { it.accuracy >= MinAccuracy.VALUE }) {
            return ProfileDescriptionTextDto(generatedText = "")
        }
        val groupAbilities = withContext(Dispatchers.IO) {
            userGroupService.getAbilityToAverageValueInGroup(userGroupId, userFilter, abilities)
        }
        if (groupAbilities.isEmpty()) {
            return ProfileDescriptionTextDto(generatedText = "")
        }
        val groupName = userGroupId?.let { userGroupService.getGroupById(it).name } ?: "csoport"

        val generated = abilitiesToTextService.generateFromAbilitiesComparedToGroup(
            userAbilities,
            groupAbilities,
            groupName,
            prompt
        )
        return ProfileDescriptionTextDto(
            generatedText = generated.response,
            prompt = generated.prompt
        )

    }

    private suspend fun generateDescriptionText(username: String, prompt: String): ChatMessageResponse {
        val userAbilities = userService.getUserDtoWithProfileByUsername(username).profile.toList()
        if (userAbilities.isEmpty()) {
            return ChatMessageResponse("", "")
        }
        return abilitiesToTextService.generateFromAbilities(userAbilities, prompt)
    }

    private fun isOlderThanOneWeek(timestamp: LocalDateTime?): Boolean {
        if (timestamp == null) return false
        val oneWeekAgo = LocalDateTime.now().minusWeeks(1)
        return timestamp.isBefore(oneWeekAgo)
    }
}