package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshot
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotItem
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private var userRepository: UserRepository,
){
    fun getAllUsers(): List<PlainUserDto>{
        return userRepository.findAll().map { PlainUserDto(it) }
    }
    fun getUserByUsername(username: String): PlainUserDto {
        return PlainUserDto(userRepository.findByUsername(username).orElseThrow())
    }
    fun getUserWithProfileByUsername(username: String): UserProfileDto {
        return UserProfileDto(userRepository.findByUsernameWithProfile(username).orElseThrow())
    }
    fun updateUser(user: PlainUserDto){
        val userEntity = userRepository.findByUsername(user.username).orElseThrow()
        userRepository.updateUserData(user.firstName, user.lastName, userEntity.id!!)
    }
    fun updateUserProfile(user: UserProfileDto): UserProfileDto {
        val userEntity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        userEntity.profile = user.profile
        val updatedEntity = userRepository.save(userEntity)
        return UserProfileDto(
            updatedEntity
        )
    }

}