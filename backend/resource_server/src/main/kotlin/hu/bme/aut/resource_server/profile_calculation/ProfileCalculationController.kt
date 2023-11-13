package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.profile_calculation.data.CalculationInfoDto
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.service.GameResultProcessingService
import hu.bme.aut.resource_server.profile_calculation.service.UserProfileUpdaterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile-calculation")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SCIENTIST')")
class ProfileCalculationController (
    @Autowired private var profileUpdaterService: UserProfileUpdaterService,
    @Autowired private var resultProcessingService: GameResultProcessingService,
    @Autowired private var dataService: ResultForCalculationDataService
){
    @GetMapping("/result_count")
    @ResponseStatus(HttpStatus.OK)
    fun getResultCountOfGame(@RequestParam("game_id") gameId: Int): Long = dataService.getCountForNewCalculation(gameId)

    @PostMapping("/process_results")
    @ResponseStatus(HttpStatus.CREATED)
    fun processResults(@RequestBody gameId: Int,): CalculationInfoDto {
        val game = dataService.getGame(gameId)
        val meanAndDeviation = resultProcessingService.processGameResults(game)
        val countOfUpdatedProfiles = dataService.getCountOfRecentCalculation(gameId)
        profileUpdaterService.updateUserProfileByResultsOfGame(game, meanAndDeviation)
        return CalculationInfoDto(meanAndDeviation, countOfUpdatedProfiles)
    }
}