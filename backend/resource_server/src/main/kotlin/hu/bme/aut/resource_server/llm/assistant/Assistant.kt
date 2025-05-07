package hu.bme.aut.resource_server.llm.assistant

interface Assistant {
    fun answerQuestion(question: String): String
}