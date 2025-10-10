package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.model.openai.OpenAiChatModel


class OpenAIAbilitiesToText(
    override val model: OpenAiChatModel
) : AbilitiesToTextService() {
}