package hu.bme.aut.resource_server.llm.assistant

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.input.PromptTemplate
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.service.AiServices
import hu.bme.aut.resource_server.authentication.AuthService
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import javax.sql.DataSource

@Service
class DataChatAssistant(
    private val chatModel: ChatLanguageModel,
    @Qualifier("aiDataSource") private val aiDatasource: DataSource,
    private val userRepository: UserRepository,
    private val authService: AuthService
) {

    suspend fun answerQuestion(question: String, authentication: Authentication): String {
        val contactUsernames = authService.getContactUsernames(authentication)
        val questionMessage = extendUserMessage(question, authentication.name, contactUsernames)
        return assistant.answerQuestion(questionMessage)
    }

    private val assistant: Assistant by lazy {
        AiServices
            .builder(Assistant::class.java)
            .chatLanguageModel(chatModel)
            .contentRetriever(createContentRetriever())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .systemMessageProvider {systemMessage}
            .build()
    }


    private fun createContentRetriever(): ContentRetriever {
        val dataStructureDescriptionFile = ClassPathResource("db_context/db_structure.txt").file
        val fileContent = dataStructureDescriptionFile.readText()
        val retriever = SqlDatabaseContentRetriever.builder()
            .dataSource(aiDatasource)
            .sqlDialect("MySQL")
            .databaseStructure(fileContent)
            .chatLanguageModel(chatModel)
            .promptTemplate(sqlPromptTemplate)
            .maxRetries(5)
            .build()
        return DelegatingRetreiver(retriever)
    }

    private val sqlPromptTemplate = PromptTemplate.from("You are an expert in writing SQL queries.\n" +
            "You have access to a {{sqlDialect}} database with the following structure:\n{{databaseStructure}}\n" +
            "If a user asks a question that can be answered by querying this database, generate an SQL SELECT query.\n" +
            "Sometimes the user wants to get information about their contacts identified by ID. For example when they ask about their " +
            "children or students data.\n In this case use their user data, " +
            "otherwise use all available data in the database.\n " +
            "Select all relevant data.\n" +
            "VERY IMPORTANT: In your answer the query should be between ``` quotation marks. And you should only write EXACTLY 1 query!" +
            "For example: Answer is this query ```SELECT COUNT(*) FROM GAME;```\n"
    );

    private val systemMessage =
        """
        Egy kedves, segítőkész szakértő asszisztens vagy, aki a felhasználó kérdéseire válaszol.
        Természetes nyelven adj választ! Ez nagyon fontos!
        Minden felhasználót azonosítóval (id) azonosítunk, a neveket és más személyes adatokat nem lehet felhasználni. Ezek az id-k NE szerepeljenek a válaszban.
        A válaszokhoz a saját tudásodat is használhatod a kognitív részképességekről, azok jelentéséről, beleértve a CHC (Cattell-Horn-Carroll) elméletet is.
        A válaszod magyarul legyen, akkor is, ha esetleg angolul kérdezik vagy az adatbázis angol nyelvű.
        """.trimIndent()


    private fun extendUserMessage(message: String, username: String, contactUsernames: List<String>): String {
        val user = userRepository.findByUsername(username).orElseThrow()
        val contactIds = userRepository.findAllByUsernameIn(contactUsernames).map { it.id }
        return """
        Felhasználó, akitől az üzenet származik (id): ${user.id}
        Üzenet: $message
        Kapcsolatban álló felhasználók azonosítója (id): ${contactIds.joinToString(", ")}, 
        de ezt figyelmen kívül kell hagyni, ha a kérdés szempontjából nem releváns.
        Nem csak ezek a felhasználók léteznek a rendszerben.
        """.trimIndent()
    }


}