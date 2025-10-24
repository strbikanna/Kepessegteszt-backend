package hu.bme.aut.resource_server.llm.chatmodel

import dev.langchain4j.model.mistralai.MistralAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class MistralChatModel(
    @Value("\${llms.mistral.api-key}") private val apiKey: String,
    @Value("\${llms.mistral.model-name}") private val modelName: String
) {

    @Bean(name = ["mistralAiChatModel"])
    fun provideMistralAiChatModel(): MistralAiChatModel {
        return MistralAiChatModel.builder()
            .apiKey(apiKey)
            .modelName(modelName)
            .build()
    }
}