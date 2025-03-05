package hu.bme.aut.resource_server.suggest

import com.fasterxml.jackson.annotation.JsonProperty

data class SuggestResponseDto(
    @JsonProperty("suggested_params")
    val config: List<Int>,

    @JsonProperty("success_rate")
    val successRate: Double
)