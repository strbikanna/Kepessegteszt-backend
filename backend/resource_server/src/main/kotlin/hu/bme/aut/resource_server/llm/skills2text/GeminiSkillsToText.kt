package hu.bme.aut.resource_server.llm.skills2text

import dev.shreyaspatil.ai.client.generativeai.GenerativeModel

class GeminiSkillsToText : SkillsToText() {
    private val apiKey: String = ""
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )

    override suspend fun generateFromPrompt(prompt: String): String {
        val result = generativeModel.generateContent(prompt).text ?: "No response from Gemini."
        log(prompt = prompt, response = result)
        return result
    }
}