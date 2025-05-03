package hu.bme.aut.resource_server.llm.chatmodel

import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

//@Configuration
class OpenAIChatModel(
    @Value("\${llms.openai.api-key}") private val apiKey: String,
    @Value("\${llms.openai.model-name}") private val modelName: String
) {
    @Bean(name = ["openAiChatModel"])
    fun provideOpenAiChatModel(): OpenAiChatModel {
        return OpenAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .build()
    }
}