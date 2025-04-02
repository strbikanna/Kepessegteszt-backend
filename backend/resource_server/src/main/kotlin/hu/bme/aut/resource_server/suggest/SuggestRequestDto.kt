package hu.bme.aut.resource_server.suggest

import com.fasterxml.jackson.annotation.JsonProperty

data class SuggestRequestDto(

    val abilities: List<Double>,

    @JsonProperty("prev_params")
    val previousParams: Map<String, Int>,

    @JsonProperty("result")
    val resultSuccess: Boolean,
)
