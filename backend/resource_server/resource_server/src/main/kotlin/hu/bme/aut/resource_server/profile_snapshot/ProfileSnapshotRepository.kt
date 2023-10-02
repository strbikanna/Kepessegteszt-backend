package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.data.repository.CrudRepository
import java.time.Instant

interface ProfileSnapshotRepository : CrudRepository<ProfileSnapshot, Long> {
    fun findAllByUser(user: UserEntity):List<ProfileSnapshot>
    fun findAllByTimestampBetween(begin: Instant, end: Instant)
}