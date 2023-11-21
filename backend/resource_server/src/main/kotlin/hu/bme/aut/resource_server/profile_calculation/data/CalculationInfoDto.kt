package hu.bme.aut.resource_server.profile_calculation.data

data class CalculationInfoDto(
    val meanAndDeviation: MeanAndDeviation,
    val updatedProfilesCount: Long
) {
}