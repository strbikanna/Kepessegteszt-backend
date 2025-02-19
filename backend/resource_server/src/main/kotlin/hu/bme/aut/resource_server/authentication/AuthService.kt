package hu.bme.aut.resource_server.authentication

import hu.bme.aut.resource_server.error.ApiCallException
import hu.bme.aut.resource_server.error.AuthException
import hu.bme.aut.resource_server.error.notContact
import hu.bme.aut.resource_server.error.removeUserFailed
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.result.ResultDto
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user_group.UserGroupRepository
import hu.bme.aut.resource_server.user_group.group.Group
import hu.bme.aut.resource_server.utils.RoleName
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * Helper for authentication related tasks.
 */
@Service
class AuthService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository,
    @Autowired private var userGroupRepository: UserGroupRepository
) {
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var AUTH_SERVER_URI: String

    var webclient: WebClient? = null

    private fun initWebClient() {
        webclient = WebClient.create(AUTH_SERVER_URI)
    }

    /**
     * Checks if the gameplay is authorized to save results for the user.
     * @throws IllegalAccessException if the game_id in the token is either not present
     * or not matching any game in the database.
     */
    @Transactional
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: ResultDto) {
        val username = authentication.name
        val dbGamePlay = recommendedGameRepository.findById(gameplay.gameplayId).orElseThrow()
        if (username != dbGamePlay.recommendedTo.username) {
            throw IllegalAccessException("This gameplay is not authorized to save result for this user.")
        }
    }

    @Transactional
    fun checkGameConfigAccessAnThrow(recommendedGameId: Long, authentication: Authentication) {
        val username = authentication.name
        val dbGamePlay = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        if (username != dbGamePlay.recommendedTo.username) {
            throw IllegalAccessException("This recommendation is not set for user: $username.")
        }
    }

    fun getAuthUser(authentication: Authentication): UserEntity {
        return userRepository.findByUsername(authentication.name).orElseThrow()
    }

    fun getAuthUserWithRoles(authentication: Authentication): UserEntity {
        return userRepository.findByUsernameWithRoles(authentication.name).orElseThrow()
    }

    fun getContactByUsername(username: String): UserEntity {
        return userRepository.findByUsername(username).orElseThrow()
    }

    fun <T> doIfIsContact(authentication: Authentication, contactUsername: String, action: () -> T): Deferred<T> =
        CoroutineScope(Dispatchers.IO).async {
            if (isContact(authentication, contactUsername)) {
                return@async action()
            } else {
                throw AuthException().notContact()
            }
        }

    /**
     * Checks if the user is authorized to see the contact's data.
     * @throws IllegalAccessException if the user is not authorized to see the contact's data.
     */
    suspend fun isContact(authentication: Authentication, contactUsername: String): Boolean {
        if (webclient == null) {
            initWebClient()
        }
        val jwt = authentication.principal as Jwt
        val accessTokenOfUser = jwt.tokenValue

        val isContact = CoroutineScope(Dispatchers.IO).async {
            val requestSpec = webclient!!
                .get()
                .uri("/user/impersonation_contacts")
                .header("Authorization", "Bearer $accessTokenOfUser")
                .accept(MediaType.APPLICATION_JSON)
            val response: Mono<String> = requestSpec.retrieve().bodyToMono(String::class.java)
            response.block()?.let {
                if (it.contains(contactUsername)) {
                    return@async true
                }
            }
            return@async false
        }
        return isContact.await()
    }

    suspend fun getContactUsernames(authentication: Authentication): List<String> {
        if (webclient == null) {
            initWebClient()
        }
        val jwt = authentication.principal as Jwt
        val accessTokenOfUser = jwt.tokenValue

        val contacts = CoroutineScope(Dispatchers.IO).async {
            val requestSpec = webclient!!
                .get()
                .uri("/user/impersonation_contacts/usernames")
                .header("Authorization", "Bearer $accessTokenOfUser")
                .accept(MediaType.APPLICATION_JSON)
            val usernamesMono = requestSpec.retrieve()
                .bodyToMono(Array<String>::class.java) // Expecting an array of strings
                .map { it.toList() }
            usernamesMono.block()?.let {
                return@async it
            }
            return@async emptyList<String>()
        }
        return contacts.await()
    }

    suspend fun removeUserFromAuthServer(authentication: Authentication) {
        if (webclient == null) {
            initWebClient()
        }
        val jwt = authentication.principal as Jwt
        val accessTokenOfUser = jwt.tokenValue

        withContext(Dispatchers.IO) {
            val requestSpec = webclient!!
                .delete()
                .uri("/user/me")
                .header("Authorization", "Bearer $accessTokenOfUser")
                .accept(MediaType.APPLICATION_JSON)
            val responseStatus = requestSpec
                .retrieve()
                .toBodilessEntity()
                .block()?.statusCode
            if (responseStatus == null || !responseStatus.is2xxSuccessful) {
                throw ApiCallException().removeUserFailed()
            }
        }
    }

    @Transactional
    fun checkUserGroupWriteAndThrow(authentication: Authentication, userGroupId: Int) {
        val user = getAuthUser(authentication)
        if (isAdmin(user)) {
            return
        }
        if (authentication.authorities.stream().noneMatch { Role.canSeeUserGroupData(it.authority) }) {
            throw IllegalAccessException("This user is not authorized to write group data.")
        }
        val uGroup = userGroupRepository.findById(userGroupId).orElseThrow()
        if (uGroup.admins.contains(user) && authentication.authorities.stream()
                .anyMatch { Role.canSeeUserGroupData(it.authority) }
        ) {
            return
        }
        throw IllegalAccessException("This user is not authorized to change this group.")
    }

    @Transactional
    fun getGroupsToAccess(authentication: Authentication): List<Group> {
        val user = getAuthUser(authentication)
        val groups = user.groups
        val allGroups = mutableListOf<Group>()
        groups.forEach { allGroups.addAll(it.getAllGroups()) }
        return allGroups.filter { it.admins.contains(user) }
    }

    @Transactional
    fun checkGroupDataReadAndThrow(authentication: Authentication, userGroupId: Int) {
        val user = getAuthUser(authentication)
        if (isAdmin(user)) {
            return
        }
        val uGroup = userGroupRepository.findById(userGroupId).orElseThrow()
        if (!(uGroup.members.contains(user) || uGroup.admins.contains(user) || uGroup.getAllUserIds()
                .contains(user.id))
        ) {
            throw IllegalAccessException("This user is not authorized to see this group's data.")
        }
    }

    private fun isAdmin(user: UserEntity): Boolean {
        return user.roles.stream().anyMatch { it.roleName == RoleName.ADMIN }
    }
}
