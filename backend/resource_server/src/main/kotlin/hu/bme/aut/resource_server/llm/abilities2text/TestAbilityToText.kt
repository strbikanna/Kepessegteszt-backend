package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.DisabledChatLanguageModel
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
@Profile("test")
class TestAbilityToText: AbilitiesToTextService() {
    override val model: ChatLanguageModel = DisabledChatLanguageModel()
}