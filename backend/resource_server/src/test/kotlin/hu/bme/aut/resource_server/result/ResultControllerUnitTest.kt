package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommendation.RecommenderService
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.utils.RoleName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.Authentication

@ExtendWith(MockitoExtension::class)
class ResultControllerUnitTest {

    @Mock
    private lateinit var resultService: ResultService

    @Mock
    private lateinit var profileSnapshotService: ProfileSnapshotService

    @Mock
    private lateinit var authService: AuthService

    @Mock
    private lateinit var recommenderService: RecommenderService

    @InjectMocks
    private lateinit var controller: ResultController

    private lateinit var authentication: Authentication

    @BeforeEach
    fun setup() {
        authentication = Mockito.mock(Authentication::class.java)
    }

    @Test
    fun `countResults as student returns only_own_count`() = runBlocking {
        val user = UserEntity(
            id = 1,
            firstName = "First",
            lastName = "Last",
            username = "authuser",
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
            roles = mutableSetOf(Role(RoleName.STUDENT))
        )
        Mockito.`when`(authService.getAuthUserWithRoles(authentication)).thenReturn(user)
        Mockito.`when`(authentication.name).thenReturn("authuser")

        Mockito.`when`(resultService.getCountByFilters(listOf("authuser"), null, null)).thenReturn(5L)

        val count = controller.countResults(authentication).await()
        assertEquals(5L, count)
    }

    @Test
    fun `countResults as admin returns_all_count`() = runBlocking {
        val user = UserEntity(
            id = 2,
            firstName = "Admin",
            lastName = "User",
            username = "admin",
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
            roles = mutableSetOf(Role(RoleName.ADMIN))
        )
        Mockito.`when`(authService.getAuthUserWithRoles(authentication)).thenReturn(user)
        Mockito.`when`(resultService.getCountByFilters(null, null)).thenReturn(10L)

        val count = controller.countResults(authentication).await()
        assertEquals(10L, count)
    }

    @Test
    fun `countResults as teacher_returns_contacts_count`() = runBlocking {
        val user = UserEntity(
            id = 3,
            firstName = "Teacher",
            lastName = "User",
            username = "teacher",
            profileFloat = mutableSetOf(),
            profileEnum = mutableSetOf(),
            roles = mutableSetOf(Role(RoleName.TEACHER))
        )
        Mockito.`when`(authService.getAuthUserWithRoles(authentication)).thenReturn(user)
        val contacts = listOf("c1", "c2")
        Mockito.`when`(authService.getContactUsernames(authentication)).thenReturn(contacts)
        Mockito.`when`(resultService.getCountByFilters(contacts, null, null)).thenReturn(7L)

        val count = controller.countResults(authentication).await()
        assertEquals(7L, count)
    }

}

