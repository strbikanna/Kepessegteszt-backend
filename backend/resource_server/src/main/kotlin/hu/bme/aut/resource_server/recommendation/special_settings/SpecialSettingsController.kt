package hu.bme.aut.resource_server.recommendation.special_settings

import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/special_settings")
class SpecialSettingsController(
    private var userRepo: UserRepository
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getSpecialSettingsOfUser(
        @RequestParam username: String
    ): SpecialSettingsDto {
        val user = userRepo.findByUsername(username).orElseThrow()
        deleteInvalidSpecialSettings(user)
        return SpecialSettingsDto(user.specialGameSettings)
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('SCIENTIST')")
    fun updateSpecialGameSettingsOfUser(
        @RequestParam username: String,
        @RequestBody specialSettings: SpecialSettingsDto
    ): SpecialSettingsDto {
        val user = userRepo.findByUsername(username).orElseThrow()
        user.specialGameSettings = specialSettings.toEntity().toMutableSet()
        userRepo.save(user)
        return SpecialSettingsDto(user.specialGameSettings)
    }

    fun deleteInvalidSpecialSettings(user: UserEntity) {
        val now = LocalDateTime.now()
        val validSettings = user.specialGameSettings.filter {
            it.creationTimestamp.plusMinutes(it.validMinutes.toLong()) > now
        }.toMutableSet()
        user.specialGameSettings = validSettings
        userRepo.save(user)
    }
}