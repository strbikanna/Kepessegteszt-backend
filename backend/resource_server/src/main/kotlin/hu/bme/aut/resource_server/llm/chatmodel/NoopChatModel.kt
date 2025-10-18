package hu.bme.aut.resource_server.llm.chatmodel

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.DisabledChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile

//@Configuration
//@Profile("test", "local")
class NoopChatModel {
    @Bean(name = ["noopChatModel"])
    fun provideNoopChatModel(): ChatModel { return DisabledChatModel()
    }
}