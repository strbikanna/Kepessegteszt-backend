package hu.bme.aut.resource_server.profile.dto

import com.fasterxml.jackson.annotation.JsonProperty
import hu.bme.aut.resource_server.ability.AbilityEntity

data class ProfileItemStatisticsDto(
    val ability: AbilityEntity,

    val mean: Double,

    @JsonProperty("deviation")
    val stdDeviation: Double,

    val individualValue: Double
) {
}