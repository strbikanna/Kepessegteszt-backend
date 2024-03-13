package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/profile_snapshot")
class ProfileSnapshotController(
    @Autowired private var profileSnapshotService: ProfileSnapshotService,
    @Autowired private var authService: AuthService
) {
    /**
     * Returns the profile snapshots of the user.
     * @param pageIndex The index of the first item.
     * @param pageSize The number of items to return.
     * @param startTime The start time of the snapshots.
     * @param endTime The end time of the snapshots.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    fun getSnapshotsOfUser(
        authentication: Authentication,
        @RequestParam(required=false) pageIndex: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?,
    ): List<ProfileSnapshotItem>{
        val user = authService.getAuthUser(authentication)
        return getSnapshotsOfUserByRequestValues(user, pageIndex, pageSize, startTime, endTime)
    }

    /**
     * Returns the profile snapshots of another user.
     * @param username The username of the user that's profile snapshots are returned.
     * @param pageIndex The index of the first item.
     * @param pageSize The number of items to return.
     * @param startTime The start time of the snapshots.
     * @param endTime The end time of the snapshots.
     */
    @GetMapping("/inspect")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT')")
    fun getSnapshotsOfOtherUsers(
        authentication: Authentication,
        @RequestParam(required = true) username: String,
        @RequestParam(required=false) pageIndex: Int?,
        @RequestParam(required = false) pageSize: Int?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startTime: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endTime: LocalDateTime?,
    ): Deferred<List<ProfileSnapshotItem>> = CoroutineScope(Dispatchers.Default).async{
        authService.checkContactAndThrow(authentication, username)
        val user = authService.getContactByUsername(username)
        return@async getSnapshotsOfUserByRequestValues(user, pageIndex, pageSize, startTime, endTime)
    }

    private fun getSnapshotsOfUserByRequestValues(
        user: UserEntity,
        pageIndex: Int?,
        pageSize: Int?,
        startTime: LocalDateTime?,
        endTime: LocalDateTime?,
    ): List<ProfileSnapshotItem>{
        if(startTime!= null && endTime != null){
            return profileSnapshotService.getSnapshotsOfUserBetween(user, startTime, endTime)
        }
        if(pageIndex != null && pageSize != null){
            return profileSnapshotService.getSnapshotsOfUser(user, pageIndex, pageSize)
        }
        return profileSnapshotService.getSnapshotsOfUser(user)
    }



}