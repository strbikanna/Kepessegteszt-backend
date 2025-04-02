package hu.bme.aut.resource_server.suggest

import com.fasterxml.jackson.annotation.JsonProperty

data class SuggestResponseDto(

    @JsonProperty("abilities_if_failure")
    val failureAbilities: List<Double>,

    @JsonProperty("abilities_if_success")
    val successAbilities: List<Double>,

    @JsonProperty("suggested_params")
    val config: Map<String, Int>,

    @JsonProperty("success_rate")
    val successRate: Double
)