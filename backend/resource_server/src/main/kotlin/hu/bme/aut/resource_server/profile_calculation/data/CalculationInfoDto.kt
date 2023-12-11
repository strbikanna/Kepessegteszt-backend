package hu.bme.aut.resource_server.profile_calculation.data

/**
 * Data transfer object for the calculation info.
 * @property meanAndDeviation The mean and deviation of the calculated profiles.
 * @property updatedProfilesCount The number of updated profiles.
 */
data class CalculationInfoDto(
    val meanAndDeviation: MeanAndDeviation,
    val updatedProfilesCount: Long
) {
}