package hu.bme.aut.resource_server.llm.chat_service

import dev.langchain4j.model.mistralai.MistralAiChatModel
import hu.bme.aut.resource_server.llm.abilities2text.AbilitiesToTextService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Service
//@Profile("!test & !local")
@Profile("local")
class MistralAbilitiesToText(
    override val model: MistralAiChatModel
) : AbilitiesToTextService() {
}