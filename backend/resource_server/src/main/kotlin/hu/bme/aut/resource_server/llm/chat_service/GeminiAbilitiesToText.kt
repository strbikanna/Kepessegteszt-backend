package hu.bme.aut.resource_server.llm.chat_service

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService

class GeminiAbilitiesToText(
    override val model: GoogleAiGeminiChatModel
) : AbilitiesToTextService() {

}