package hu.bme.aut.resource_server.llm.skills2text

import hu.bme.aut.resource_server.llm.mistral_kmp.MistralClient
import hu.bme.aut.resource_server.llm.mistral_kmp.domain.Message
import hu.bme.aut.resource_server.llm.mistral_kmp.domain.ModelParams
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class MistralAbilitiesToText(
    @Value("\${llms.mistral.api-key}") key: String,
    @Value("\${llms.mistral.model-name}") private val model: String
) : AbilitiesToTextService(key) {
    private val mistral by lazy { MistralClient(apiKey = apiKey) }

    override suspend fun generateFromPrompt(prompt: String): String {

        val chatResult = mistral.chat(
            model = model,
            messages = listOf(Message(content = prompt)),
            params = ModelParams(safePrompt = false),
        )
        val result = chatResult.getOrNull()?.get(0)?.content ?: "No response from Mistral."
        log(prompt = prompt, response = result)
        return result
    }
}