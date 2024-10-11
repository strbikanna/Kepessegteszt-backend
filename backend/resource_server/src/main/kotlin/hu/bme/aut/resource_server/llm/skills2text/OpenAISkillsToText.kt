package hu.bme.aut.resource_server.llm.skills2text

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

class OpenAISkillsToText(
    key: String,
    private val model: String = "gpt-4o-mini"
) : SkillsToText(key) {
    private val systemMessage = ChatMessage(
        role = ChatRole.System,
        content = "You are a helpful assistant!"
    )
    private val openai by lazy { OpenAI(token = apiKey) }

    override suspend fun generateFromPrompt(prompt: String): String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(model),
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