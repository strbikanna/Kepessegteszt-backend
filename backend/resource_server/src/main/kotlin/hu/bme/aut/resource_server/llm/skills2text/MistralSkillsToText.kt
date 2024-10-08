package hu.bme.aut.resource_server.llm.skills2text

import hu.bme.aut.resource_server.llm.mistral_kmp.MistralClient
import hu.bme.aut.resource_server.llm.mistral_kmp.domain.Message
import hu.bme.aut.resource_server.llm.mistral_kmp.domain.ModelParams

class MistralSkillsToText : SkillsToText() {
    private val apiKey: String = ""
    private val mistral = MistralClient(apiKey = apiKey)

    override suspend fun generateFromPrompt(prompt: String): String {

        val chatResult = mistral.chat(
            model = "open-mistral-7b",
            messages = listOf(Message(content = prompt)),
            params = ModelParams(safePrompt = false),
        )
        val result = chatResult.getOrNull()?.get(0)?.content ?: "No response from Mistral."
        log(prompt = prompt, response = result)
        return result
    }
}