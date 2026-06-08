package hu.bme.aut.resource_server.recommendation.special_settings

import hu.bme.aut.resource_server.recommendation.RecommenderService
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.BusinessCritical
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/special_settings")
class SpecialSettingsController(
    private var userRepo: UserRepository,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    fun getSpecialSettingsOfUser(
        @RequestParam username: String
    ): SpecialSettingsDto {
        val user = userRepo.findByUsernameWithSpecialSettings(username).orElseThrow()
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
        val user = userRepo.findByUsernameWithSpecialSettings(username).orElseThrow()
        user.specialGameSettings = specialSettings.toEntity().toMutableSet()
        userRepo.save(user)
        return SpecialSettingsDto(user.specialGameSettings)
    }

    @Scheduled(cron = "0 0 2 * * *")
    @BusinessCritical
    fun refreshSpecialSettingsOfAllUser() {
        userRepo.findAllUsernames().forEach { username ->
            val user = userRepo.findByUsernameWithSpecialSettings(username).get()
            user.specialGameSettings = user.specialGameSettings.filter { it.isValid() }.toMutableSet()
            userRepo.save(user)
        }
    }

    fun deleteInvalidSpecialSettings(user: UserEntity) {
        val validSettings = user.specialGameSettings.filter {
            it.isValid()
        }.toMutableSet()
        user.specialGameSettings = validSettings
        userRepo.save(user)
    }
}