package hu.bme.aut.resource_server.llm.skills2text

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import org.springframework.beans.factory.annotation.Value

class GeminiAbilitiesToText(
    @Value("\${llms.gemini.api-key}") key: String,
    @Value("\${llms.gemini.model-name}") model: String
) : AbilitiesToTextService(key) {
    private val generativeModel = GenerativeModel(
        modelName = model,
        apiKey = apiKey
    )

    override suspend fun generateFromPrompt(prompt: String): String {
        val result = generativeModel.generateContent(prompt).text ?: "No response from Gemini."
        log(prompt = prompt, response = result)
        return result
    }
}