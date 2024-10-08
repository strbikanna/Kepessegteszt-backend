package hu.bme.aut.resource_server.llm.skills2text

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class OpenAISkillsToText : SkillsToText() {
    private val apiKey: String = ""
    private val systemMessage = ChatMessage(
        role = ChatRole.System,
        content = "You are a helpful assistant!"
    )
    private val openai = OpenAI(
        token = apiKey
    )

    override suspend fun generateFromPrompt(prompt: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4o-mini"),
            messages = listOf(
                systemMessage,
                ChatMessage(
                    role = ChatRole.User,
                    content = prompt
                )
            )
        )
        val completion: ChatCompletion = openai.chatCompletion(chatCompletionRequest)
        val result = completion.choices.first().message.content ?: "No response from OpenAI."
        log(prompt = prompt, response = result)
        return result
    }
}