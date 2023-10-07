package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshot
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotItem
import hu.bme.aut.resource_server.profile_snapshot.ProfileSnapshotRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var profileSnapshotRepository: ProfileSnapshotRepository,
){
    fun getAllUsers(): List<PlainUserDto>{
        return userRepository.findAll().map { PlainUserDto(it) }
    }
    fun getUserById(id: Int): PlainUserDto {
        return PlainUserDto(userRepository.findById(id).orElseThrow())
    }
    fun getUserWithProfileById(id: Int): UserProfileDto {
        return UserProfileDto(userRepository.findByIdWithProfile(id).orElseThrow())
    }
    fun updateUser(user: PlainUserDto): PlainUserDto {
        val userEntity = userRepository.findByUsername(user.username).orElseThrow()
        return PlainUserDto(
            userRepository.updateUserData(user.firstName, user.lastName, userEntity.id!!)
        )
    }
    fun updateUserProfile(user: UserProfileDto): UserProfileDto {
        val userEntity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        userEntity.profile = user.profile
        return UserProfileDto(
            userRepository.save(userEntity)
        )
    }

    fun saveSnapshotFromUser(user: UserEntity): ProfileSnapshot{
        val profile = user.profile
        val profileSnapshotItems = profile.map { ProfileSnapshotItem(ability = it.ability, abilityValue = it.abilityValue) }.toMutableSet()
        val profileSnapshot = ProfileSnapshot(user = user, profile = profileSnapshotItems)
        return profileSnapshotRepository.save(profileSnapshot)
    }

    fun saveSnapshotFromUser(user: UserProfileDto): ProfileSnapshot{
        val entity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        return saveSnapshotFromUser(entity)
    }
}