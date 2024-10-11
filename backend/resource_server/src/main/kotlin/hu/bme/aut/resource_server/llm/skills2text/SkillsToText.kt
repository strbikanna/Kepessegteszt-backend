package hu.bme.aut.resource_server.llm.skills2text

import hu.bme.aut.resource_server.profile.ProfileItem

abstract class SkillsToText(
    protected val apiKey: String = ""
) {
    protected open val promptTemplate = "Convert the following Cattell–Horn–Carroll (CHC) theory abilities' " +
                "values into a small summary about the person's cognitive skills.\n" +
                "For every skill, the average value is 1.0. The levels change with 0.15 sized gaps.\n" +
                "This person's skills adn values are:\n"
    private var islLoggingEnabled = false
    private var logger: (prompt: String, response: String) -> Unit
            = { prompt, response -> println("Prompt:\n$prompt\nResponse:\n$response\n\n") }

    fun setLogging(logging: Boolean, newLogger: ((String, String) -> Unit)? = null) {
        if (newLogger != null) {
            logger = newLogger
        }
        islLoggingEnabled = logging
    }

    protected fun log(prompt: String, response: String) {
        if (islLoggingEnabled) {
            logger(prompt, response)
        }
    }

    open suspend fun generateFromSkills(skills: List<ProfileItem>, prompt: String = ""): String {
        var callPrompt = prompt.ifBlank { promptTemplate }
        for (skill in skills) {
            callPrompt += "${skill.ability.name}, description: ${skill.ability.description}, value: ${skill.value}\n"
        }
        return generateFromPrompt(callPrompt)
    }

    abstract suspend fun generateFromPrompt(prompt: String): String
}
