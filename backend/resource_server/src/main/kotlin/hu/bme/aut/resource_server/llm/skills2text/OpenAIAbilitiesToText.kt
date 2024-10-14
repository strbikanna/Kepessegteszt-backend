package hu.bme.aut.resource_server.llm.skills2text

import dev.langchain4j.model.openai.OpenAiChatModel
import org.springframework.beans.factory.annotation.Value

class OpenAIAbilitiesToText(
    @Value("\${llms.openai.api-key}")apiKey: String,
    @Value("\${llms.openai.model-name}") modelName: String
) : AbilitiesToTextService() {
    override val model: OpenAiChatModel = OpenAiChatModel.builder()
        .apiKey(apiKey)
        .modelName(modelName)
        .build()
}