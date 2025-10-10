package hu.bme.aut.resource_server.llm.chat_service

import dev.langchain4j.model.chat.ChatLanguageModel
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import org.springframework.stereotype.Service

//@Profile("test", "local")
@Service
class TestAbilitiesToTextService(
    override val model: ChatLanguageModel
): AbilitiesToTextService() {
}