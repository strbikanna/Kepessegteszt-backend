package hu.bme.aut.resource_server.llm.chatmodel

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean


//@Configuration
class GeminiChatModel(
    @Value("\${llms.gemini.api-key}") private val apiKey: String,
    @Value("\${llms.gemini.model-name}") private val modelName: String
) {
    @Bean(name = ["geminiAiChatModel"])
    fun provideGeminiAiChatModel(): GoogleAiGeminiChatModel {
        return GoogleAiGeminiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .build()
    }
}