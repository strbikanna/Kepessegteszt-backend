package hu.bme.aut.resource_server.llm

import hu.bme.aut.resource_server.llm.abilities2text.ChatMessageResponse
import hu.bme.aut.resource_server.llm.assistant.DataChatAssistant
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/assistant")
class AssistantController(
    private val chatAssistant: DataChatAssistant,
) {

    @PostMapping("/question")
    @ResponseStatus(HttpStatus.OK)
    suspend fun answerQuestion(
        @RequestBody question: String,
        authentication: Authentication
    ): ChatMessageResponse {
        return ChatMessageResponse(
            prompt = question,
            response = chatAssistant.answerQuestion(question, authentication)
        )
    }

}