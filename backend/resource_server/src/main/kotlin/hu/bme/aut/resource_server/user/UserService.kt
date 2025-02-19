package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotService
import hu.bme.aut.resource_server.recommended_game.RecommendedGameService
import hu.bme.aut.resource_server.result.ResultService
import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private var userRepository: UserRepository,
    private val profileSnapshotService: ProfileSnapshotService,
    private val recommendedGameService: RecommendedGameService,
    private val resultService: ResultService,
){
    fun getAllUsers(): List<PlainUserDto>{
        return userRepository.findAll().map { PlainUserDto(it) }
    }
    fun getUserDtoByUsername(username: String): PlainUserDto {
        return PlainUserDto(userRepository.findByUsername(username).orElseThrow())
    }
    fun getUserDtoWithProfileByUsername(username: String): UserProfileDto {
        return UserProfileDto(userRepository.findByUsernameWithProfile(username).orElseThrow())
    }
    fun getUserEntityWithProfileByUsername(username: String): UserEntity {
        return userRepository.findByUsernameWithProfile(username).orElseThrow()
    }

    fun updateUser(user: PlainUserDto){
        val userEntity = userRepository.findByUsername(user.username).orElseThrow()
        userRepository.updateUserData(
            user.firstName,
            user.lastName,
            user.address ?: userEntity.address,
            user.birthDate ?: userEntity.birthDate,
            user.gender ?: userEntity.gender,
            userEntity.id!!)
    }
    fun updateUserProfile(user: UserEntity): UserProfileDto {
        val userEntity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        userEntity.profileEnum = user.profileEnum
        userEntity.profileFloat = user.profileFloat
        val updatedEntity = userRepository.save(userEntity)
        return UserProfileDto(
            updatedEntity
        )
    }

    @Transactional
    fun removeUserForever(username: String){
        val user = userRepository.findByUsername(username).orElseThrow()

        user.groups.clear()
        user.organizations.clear()
        user.profileFloat.clear()
        user.profileEnum.clear()
        user.roles.clear()
        userRepository.save(user)

        profileSnapshotService.deleteAllSnapshotsOfUser(user)
        recommendedGameService.deleteAllRecommendationsByUser(user)
        resultService.deleteAllResultsOfUser(user)

        userRepository.deleteAllByUsername(username)
    }

}