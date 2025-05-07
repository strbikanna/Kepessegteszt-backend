package hu.bme.aut.resource_server.llm.assistant

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever
import dev.langchain4j.rag.content.Content
import dev.langchain4j.rag.content.DefaultContent
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.query.Query
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DelegatingRetreiver(
    private val delegate: SqlDatabaseContentRetriever
) : ContentRetriever {

    private val logger: Logger = LoggerFactory.getLogger(DelegatingRetreiver::class.java)

    override fun retrieve(p0: Query?): MutableList<Content> {
        logger.info("Retrieving content with query: $p0")
        var result = delegate.retrieve(p0)
        if (result.isEmpty()) {
            logger.warn("No content retrieved for query: $p0")
            result = delegate.retrieve(p0)
        }
        logger.info("Retrieved content: $result")
        val resultValue = result.map {  content ->
                val contentText = content.textSegment().text().split("\n")
                DefaultContent(
                    contentText[contentText.lastIndex]
                ) as Content
            }.toMutableList()
        logger.info("Transformed content: $resultValue")
        return resultValue
    }
}