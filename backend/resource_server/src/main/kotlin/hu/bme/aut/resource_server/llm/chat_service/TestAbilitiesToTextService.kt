package hu.bme.aut.resource_server.llm.chat_service

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.DisabledChatModel
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("test", "local")
//@Profile("!test & !local")
@Service
class TestAbilitiesToTextService(
    override val model: ChatModel = DisabledChatModel()
): AbilitiesToTextService() {
}