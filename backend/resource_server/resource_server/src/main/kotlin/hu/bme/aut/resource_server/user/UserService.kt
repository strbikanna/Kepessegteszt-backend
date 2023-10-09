package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private var userRepository: UserRepository,
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
        userRepository.updateUserData(user.firstName, user.lastName, userEntity.id!!)
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

}