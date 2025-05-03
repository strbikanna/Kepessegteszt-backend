package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.model.chat.ChatLanguageModel
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("test", "local")
@Service
class TestAbilitiesToTextService(
    override val model: ChatLanguageModel
): AbilitiesToTextService() {
}