package hu.bme.aut.resource_server.profile_calculation

import hu.bme.aut.resource_server.profile_calculation.data.CalculationInfoDto
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationDataService
import hu.bme.aut.resource_server.profile_calculation.service.GameResultProcessingService
import hu.bme.aut.resource_server.profile_calculation.service.UserProfileUpdaterService
import hu.bme.aut.resource_server.recommendation.AutoRecommendationService
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile-calculation")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SCIENTIST')")
class ProfileCalculationController(
    @Autowired private var profileUpdaterService: UserProfileUpdaterService,
    @Autowired private var resultProcessingService: GameResultProcessingService,
    @Autowired private var autoRecommendationService: AutoRecommendationService,
    @Autowired private var dataService: ResultForCalculationDataService
) {

    /**
     * Returns the count of results that are not yet processed for the given game.
     */
    @GetMapping("/result_count")
    @ResponseStatus(HttpStatus.OK)
    fun getResultCountOfGame(@RequestParam gameId: Int): Long = dataService.getCountForNewCalculation(gameId)

    /**
     * Processes the results of the given game and returns the mean and standard deviation of the results
     * and the count of updated profiles.
     */
    @PostMapping("/process_results")
    @ResponseStatus(HttpStatus.CREATED)
    fun processResults(@RequestParam gameId: Int): Deferred<CalculationInfoDto> =
        CoroutineScope(Dispatchers.Default).async {
            val meanAndDeviation = resultProcessingService.processGameResults(gameId)
            val countOfUpdatedProfiles = dataService.getCountOfRecentCalculation(gameId)
            profileUpdaterService.updateUserProfileByResultsOfGame(gameId, meanAndDeviation)
            //autoRecommendationService.createRecommendationModel(gameId)
            return@async CalculationInfoDto(meanAndDeviation, countOfUpdatedProfiles)
        }
}