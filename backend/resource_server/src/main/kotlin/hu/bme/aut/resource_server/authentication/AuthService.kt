package hu.bme.aut.resource_server.authentication

import hu.bme.aut.resource_server.gameplayresult.GameplayResultDto
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.user_group.UserGroupRepository
import hu.bme.aut.resource_server.user_group.group.Group
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: GameplayResultDto) {
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

    fun getContactByUsername(username: String): UserEntity {
        return userRepository.findByUsername(username).orElseThrow()
    }

    /**
     * Checks if the user is authorized to see the contact's data.
     * @throws IllegalAccessException if the user is not authorized to see the contact's data.
     */
    suspend fun checkContactAndThrow(authentication: Authentication, contactUsername: String) {
        if (webclient == null) {
            initWebClient()
        }
        val jwt = authentication.principal as Jwt
        val accessTokenOfUser = jwt.tokenValue

        CoroutineScope(Dispatchers.IO).launch {
            val requestSpec = webclient!!
                    .get()
                    .uri("/user/impersonation_contacts")
                    .header("Authorization", "Bearer $accessTokenOfUser")
                    .accept(MediaType.APPLICATION_JSON)
            val response: Mono<String> = requestSpec.retrieve().bodyToMono(String::class.java)
            response.block()?.let {
                if (it.contains(contactUsername)) {
                    return@launch
                }
            }
            throw IllegalAccessException("This user is not authorized to see this contact.")
        }

    }

    @Transactional
    fun canAccessUserGroup(authentication: Authentication, userGroupId: Int) {
        val user = getAuthUser(authentication)
        val uGroup = userGroupRepository.findById(userGroupId).orElseThrow()
        if (uGroup.admins.contains(user) && authentication.authorities.stream().anyMatch { Role.canSeeUserGroupData(it.authority) }) {
            return
        }
        throw IllegalAccessException("This user is not authorized to see this group.")
    }

    @Transactional
    fun getGroupsToAccess(authentication: Authentication): List<Group> {
        if (authentication.authorities.stream().noneMatch { Role.canSeeUserGroupData(it.authority) }){
            throw IllegalAccessException("This user is not authorized to see group data.")
        }
        val user = getAuthUser(authentication)
        val groups = user.groups
        val allGroups = mutableListOf<Group>()
        groups.forEach { allGroups.addAll(it.getAllGroups()) }
        return allGroups.filter { it.admins.contains(user) }
    }

    @Transactional
    fun canSeeUserGroupData(authentication: Authentication, userGroupId: Int): Boolean {
        val user = getAuthUser(authentication)
        val uGroup = userGroupRepository.findById(userGroupId).orElseThrow()
        return uGroup.members.contains(user) || uGroup.admins.contains(user) || uGroup.getAllUserIds().contains(user.id)
    }
}