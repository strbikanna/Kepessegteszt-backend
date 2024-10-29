package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.model.mistralai.MistralAiChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MistralAbilitiesToText(
    @Value("\${llms.mistral.api-key}") apiKey: String,
    @Value("\${llms.mistral.model-name}") modelName: String
) : AbilitiesToTextService() {
    override val model: MistralAiChatModel = MistralAiChatModel.builder()
        .apiKey(apiKey)
        .modelName(modelName)
        .build()
}