package hu.bme.aut.resource_server.authentication

import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.gameplayresult.GameplayResultDto
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
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


@Service
class AuthService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var gameRepository: GameRepository,
    @Autowired private var userRepository: UserRepository
) {
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var AUTH_SERVER_URI: String;

    var webclient : WebClient? = null

    private fun initWebClient(){
        webclient = WebClient.create(AUTH_SERVER_URI)
    }

    /**
     * Checks if the gameplay is authorized to save results for the user.
     * @throws IllegalAccessException if the game_id in the token is either not present
     * or not matching any game in the database.
     */
    @Transactional
    fun checkGameAccessAndThrow(authentication: Authentication, gameplay: GameplayResultDto){
        val username = authentication.name
        val gamePlay = recommendedGameRepository.findById(gameplay.gameplayId)
        val jwt = authentication.principal as Jwt
        val tokenGameId = Integer.parseInt(jwt.claims["game_id"] as String?
            ?: throw IllegalAccessException("This gameplay is not authorized to save result for this user."))
        val game = gameRepository.findById(tokenGameId)
        if(gamePlay.isEmpty || username == null ||game.isEmpty || tokenGameId != gamePlay.get().game.id){
            throw IllegalAccessException("This gameplay is not authorized to save result for this user.")
        }
    }
    fun getAuthUser(authentication: Authentication): UserEntity{
        return userRepository.findByUsername(authentication.name).orElseThrow()
    }
    fun getContactByUsername(username: String): UserEntity{
        return userRepository.findByUsername(username).orElseThrow()
    }
    suspend fun checkContactAndThrow(authentication: Authentication, contactUsername: String){
        if(webclient==null){
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
}