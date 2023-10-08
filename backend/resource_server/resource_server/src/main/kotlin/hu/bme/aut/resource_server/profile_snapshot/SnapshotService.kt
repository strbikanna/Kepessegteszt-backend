package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.user.PlainUserDto
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserProfileDto
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SnapshotService(
    @Autowired private var userRepository: UserRepository,
    @Autowired private var profileSnapshotRepository: ProfileSnapshotRepository,
) {
    fun saveSnapshotOfUser(user: UserEntity): ProfileSnapshot{
        val profile = user.profile
        val profileSnapshotItems = profile.map { ProfileSnapshotItem(ability = it.ability, abilityValue = it.abilityValue) }.toSet()
        val profileSnapshot = ProfileSnapshot(user = user, profile = profileSnapshotItems)
        return profileSnapshotRepository.save(profileSnapshot)
    }

    fun saveSnapshotOfUser(user: UserProfileDto): ProfileSnapshot{
        val entity = userRepository.findByUsernameWithProfile(user.username).orElseThrow()
        return saveSnapshotOfUser(entity)
    }

    fun getSnapshotsOfUser(user: PlainUserDto): List<ProfileSnapshot>{
        val entity = userRepository.findByUsername(user.username).orElseThrow()
        return profileSnapshotRepository.findAllByUser(entity)
    }

    fun getSnapshotsOfUserBetween(user: PlainUserDto, begin: Instant, end: Instant): List<ProfileSnapshot>{
        val entity = userRepository.findByUsername(user.username).orElseThrow()
        return profileSnapshotRepository.findAllByUserAndTimestampBetween(entity, begin, end)
    }
}