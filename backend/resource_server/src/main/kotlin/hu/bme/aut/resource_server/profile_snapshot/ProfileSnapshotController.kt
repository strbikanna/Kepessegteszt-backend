package hu.bme.aut.resource_server.profile_snapshot

import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.UserEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/profile_snapshot")
class ProfileSnapshotController(
    @Autowired private var profileSnapshotService: ProfileSnapshotService,
    @Autowired private var authService: AuthService
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ROLE_SCIENTIST', 'ROLE_TEACHER', 'ROLE_PARENT', 'ROLE_STUDENT')")
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
    ): List<ProfileSnapshotItem>{
        authService.checkContactAndThrow(authentication, username)
        val user = authService.getContactByUsername(username)
        return getSnapshotsOfUserByRequestValues(user, pageIndex, pageSize, startTime, endTime)
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