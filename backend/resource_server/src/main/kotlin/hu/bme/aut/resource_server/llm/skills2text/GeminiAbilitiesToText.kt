package hu.bme.aut.resource_server.llm.skills2text

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import org.springframework.beans.factory.annotation.Value

class GeminiAbilitiesToText(
    @Value("\${llms.gemini.api-key}") apiKey: String,
    @Value("\${llms.gemini.model-name}") modelName: String
) : AbilitiesToTextService() {
    override val model: GoogleAiGeminiChatModel = GoogleAiGeminiChatModel.builder()
        .apiKey(apiKey)
        .modelName(modelName)
        .build()
}