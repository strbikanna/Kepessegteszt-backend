package hu.bme.aut.resource_server.llm.abilities2text

data class ChatMessageResponse(
    val prompt: String,
    val response: String
)