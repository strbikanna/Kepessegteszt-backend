package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel

class GeminiAbilitiesToText(
    override val model: GoogleAiGeminiChatModel
) : AbilitiesToTextService() {

}