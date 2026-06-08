package hu.bme.aut.resource_server.profile.description_text

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import hu.bme.aut.resource_server.llm.abilities2text.ChatMessageResponse
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserGroupDataService
import hu.bme.aut.resource_server.user.UserService
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.utils.RoleName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ProfileDescriptionServiceTest {

    @Mock
    private lateinit var descriptionRepository: ProfileDescriptionTextRepository

    @Mock
    private lateinit var abilitiesToTextService: AbilitiesToTextService

    @Mock
    private lateinit var userService: UserService

    @Mock
    private lateinit var userGroupService: UserGroupDataService

    @InjectMocks
    private lateinit var service: ProfileDescriptionService

    private val username = "testuser"

    @Test
    fun `returns existing recent description from db`(): Unit = runBlocking {
        val existing = ProfileDescriptionTextEntity(
            id = 1,
            generatedText = "old text",
            timestamp = LocalDateTime.now().minusDays(2),
            user = createTestUser()
        )
        Mockito.`when`(descriptionRepository.findAllByUserUsername(username)).thenReturn(listOf(existing))

        val result = service.getProfileDescriptionOfUser(username)

        assertEquals(existing.generatedText, result.generatedText)
        Mockito.verify(descriptionRepository, Mockito.never())
            .save(ArgumentMatchers.any(ProfileDescriptionTextEntity::class.java))
    }

    @Test
    fun `generates and updates when existing description is older than a week`(): Unit = runBlocking {
        val old = ProfileDescriptionTextEntity(
            id = 1,
            generatedText = "old text",
            timestamp = LocalDateTime.now().minusWeeks(2),
            user = createTestUser()
        )
        Mockito.`when`(descriptionRepository.findAllByUserUsername(username)).thenReturn(listOf(old))

        Mockito.`when`(descriptionRepository.save(ArgumentMatchers.any(ProfileDescriptionTextEntity::class.java)))
            .thenAnswer { invocation -> invocation.getArgument(0) }

        val generated = ChatMessageResponse("prompt", "new generated text")
        Mockito.`when`(userService.getUserEntityWithProfileByUsername(username)).thenReturn(createTestUser())
        Mockito.`when`(userService.getUserDtoWithProfileByUsername(username))
            .thenReturn(UserProfileDto(createTestUser()))
        Mockito.`when`(
            abilitiesToTextService.generateFromAbilities(
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString()
            )
        )
            .thenReturn(generated)

        val result = service.getProfileDescriptionOfUser(username)

        assertEquals(generated.response, result.generatedText)
        Mockito.verify(descriptionRepository).deleteAll(listOf(old))
        Mockito.verify(descriptionRepository).save(ArgumentMatchers.any(ProfileDescriptionTextEntity::class.java))
    }

    @Test
    fun `generates and saves when no existing description`(): Unit = runBlocking {
        Mockito.`when`(descriptionRepository.findAllByUserUsername(username)).thenReturn(emptyList())
        Mockito.`when`(descriptionRepository.save(ArgumentMatchers.any(ProfileDescriptionTextEntity::class.java)))
            .thenAnswer { invocation -> invocation.getArgument(0) }

        val generated = ChatMessageResponse("p", "fresh text")
        Mockito.`when`(userService.getUserEntityWithProfileByUsername(username)).thenReturn(createTestUser())
        Mockito.`when`(userService.getUserDtoWithProfileByUsername(username))
            .thenReturn(UserProfileDto(createTestUser()))
        Mockito.`when`(
            abilitiesToTextService.generateFromAbilities(
                ArgumentMatchers.anyList(),
                ArgumentMatchers.anyString()
            )
        )
            .thenReturn(generated)

        val result = service.getProfileDescriptionOfUser(username)

        assertEquals(generated.response, result.generatedText)
        Mockito.verify(descriptionRepository, Mockito.never()).deleteAll(ArgumentMatchers.any())
        Mockito.verify(descriptionRepository).save(ArgumentMatchers.any(ProfileDescriptionTextEntity::class.java))
    }

    private fun createTestUser(): UserEntity = UserEntity(
        id = 1,
        firstName = "Test",
        lastName = "User",
        username = username,
        birthDate = null,
        gender = null,
        address = null,
        profileFloat = mutableSetOf(
            FloatProfileItem(
                id = 1,
                ability = AbilityEntity(code = "Gf", name = "Fluid Intelligence", description = ""),
                abilityValue = 0.5
            )
        ),
        profileEnum = mutableSetOf(),
        roles = mutableSetOf(Role(RoleName.STUDENT)),
        subscription = null,
        organizations = mutableSetOf(),
        groups = mutableSetOf(),
        specialGameSettings = mutableSetOf(),
        xP = 0
    )
}

