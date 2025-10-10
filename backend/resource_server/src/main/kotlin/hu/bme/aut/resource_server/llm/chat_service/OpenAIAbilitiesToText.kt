package hu.bme.aut.resource_server.llm.chat_service

import dev.langchain4j.model.openai.OpenAiChatModel
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService


class OpenAIAbilitiesToText(
    override val model: OpenAiChatModel
) : AbilitiesToTextService() {
}