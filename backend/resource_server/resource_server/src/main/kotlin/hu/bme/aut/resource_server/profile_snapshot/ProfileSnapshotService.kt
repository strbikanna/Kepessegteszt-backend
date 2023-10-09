package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ProfileSnapshotService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var floatProfileSnapshotRepository: FloatProfileSnapshotRepository,
    @Autowired private var enumProfileSnapshotRepository: EnumProfileSnapshotRepository,
) {
    fun saveSnapshotOfUser(user: UserEntity){
        saveFloatProfile(user)
        saveEnumProfile(user)
    }

    private fun saveEnumProfile(user: UserEntity) {
        val enumProfile = user.profileEnum
        val enumProfileSnapshotItems = enumProfile.map {
            EnumProfileSnapshotItem(it, user)
        }
        enumProfileSnapshotRepository.saveAll(enumProfileSnapshotItems)
    }

    private fun saveFloatProfile(user: UserEntity) {
        val floatProfile = user.profileFloat
        val profileSnapshotItems = floatProfile.map {
            FloatProfileSnapshotItem(it, user)
        }
        floatProfileSnapshotRepository.saveAll(profileSnapshotItems)
    }

    fun saveSnapshotOfUser(user: UserProfileDto){
        val entity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        saveSnapshotOfUser(entity)
    }

    fun getSnapshotsOfUser(user: PlainUserDto): List<ProfileSnapshotItem>{
        val entity = userRepository.findByUsername(user.username).orElseThrow()
        val snapShots = mutableListOf<ProfileSnapshotItem>()
        val floatSnapshots = floatProfileSnapshotRepository.findAllByUser(entity)
        val enumSnapshots = enumProfileSnapshotRepository.findAllByUser(entity)
        snapShots.addAll(floatSnapshots)
        snapShots.addAll(enumSnapshots)
        return snapShots
    }

    fun getSnapshotsOfUserBetween(user: PlainUserDto, begin: LocalDateTime, end: LocalDateTime): List<ProfileSnapshotItem>{
        val entity = userRepository.findByUsername(user.username).orElseThrow()
        val snapShots = mutableListOf<ProfileSnapshotItem>()
        val floatSnapshots = floatProfileSnapshotRepository.findAllByUserAndTimestampBetween(entity, begin, end)
        val enumSnapshots = enumProfileSnapshotRepository.findAllByUserAndTimestampBetween(entity, begin, end)
        snapShots.addAll(floatSnapshots)
        snapShots.addAll(enumSnapshots)
        return snapShots
    }
}