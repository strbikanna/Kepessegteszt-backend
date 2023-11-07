package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.user.user_dto.PlainUserDto
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.user_dto.UserProfileDto
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
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
    fun saveSnapshotOfUser(username: String){
        val entity = userRepository.findByUsernameWithProfile(username).orElseThrow()
        saveSnapshotOfUser(entity)
    }

    fun getSnapshotsOfUser(user: PlainUserDto): List<ProfileSnapshotItem>{
        val entity = userRepository.findByUsername(user.username).orElseThrow()
        return getSnapshotsOfUser(entity)
    }
    fun getSnapshotsOfUser(user: UserEntity, pageIndex: Int = 0, pageSize: Int = 10): List<ProfileSnapshotItem>{
        val snapShots = mutableListOf<ProfileSnapshotItem>()
        val floatSnapshots = floatProfileSnapshotRepository.findAllPagedByUser(user, PageRequest.of(pageIndex, pageSize))
        val enumSnapshots = enumProfileSnapshotRepository.findAllPagedByUser(user, PageRequest.of(pageIndex, pageSize))
        snapShots.addAll(floatSnapshots)
        snapShots.addAll(enumSnapshots)
        return snapShots
    }

    fun getSnapshotsOfUserBetween(user: UserEntity, begin: LocalDateTime, end: LocalDateTime): List<ProfileSnapshotItem>{
        val snapShots = mutableListOf<ProfileSnapshotItem>()
        val floatSnapshots = floatProfileSnapshotRepository.findAllByUserAndTimestampBetween(user, begin, end)
        val enumSnapshots = enumProfileSnapshotRepository.findAllByUserAndTimestampBetween(user, begin, end)
        snapShots.addAll(floatSnapshots)
        snapShots.addAll(enumSnapshots)
        return snapShots
    }

    fun existsSnapshotToday(username: String):Boolean{
        val user = userRepository.findByUsername(username).orElseThrow()
        val todayStart = getTodayStart()
        val todayEnd = getTodayEnd()
        return floatProfileSnapshotRepository.existsByUserAndTimestampBetween(user, todayStart, todayEnd)
                && enumProfileSnapshotRepository.existsByUserAndTimestampBetween(user, todayStart, todayEnd)
    }
    private fun getTodayStart() = LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            0,0,0
        )
    private fun getTodayEnd() = LocalDateTime.of(
            LocalDateTime.now().year,
            LocalDateTime.now().month,
            LocalDateTime.now().dayOfMonth,
            23,59,59
        )

}