package hu.bme.aut.resource_server.llm.chatmodel

import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.DisabledChatLanguageModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

//@Configuration
@Profile("test", "local")
class NoopChatModel {
    @Bean(name = ["noopChatModel"])
    fun provideNoopChatModel(): ChatLanguageModel { return DisabledChatLanguageModel()}
}