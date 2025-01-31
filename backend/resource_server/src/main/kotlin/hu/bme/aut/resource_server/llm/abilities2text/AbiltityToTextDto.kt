package hu.bme.aut.resource_server.llm.abilities2text

data class AbiltityToTextDto(
    val prompt: String,
    val abilitiesAsText: String
)