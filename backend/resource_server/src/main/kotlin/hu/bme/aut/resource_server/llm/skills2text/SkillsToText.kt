package hu.bme.aut.resource_server.llm.skills2text

import hu.bme.aut.resource_server.profile.ProfileItem

abstract class SkillsToText(
    protected val apiKey: String = ""
) {
    protected open val promptTemplate = "Alakítsd át a következő Cattell–Horn–Carroll (CHC) teória képességértékeit " +
            "egy rövid összefoglalóvá a személy kognitív adottságairól!\n" +
            "Minden képesség átlagos értéke 1,0. A szintek 0,15-ös léptékekkel változnak.\n" +
            "Ennek a személynek a képességei és azok értékei a következők:\n"
    protected open val promptTemplateWithGroup = "Alakítsd át a következő Cattell–Horn–Carroll (CHC) teória képességértékeit " +
            "egy rövid összefoglalóvá a személy kognitív adottságairól az adott társadalmi csoportjához képest!\n" +
            "Minden képesség átlagos értéke 1,0. A szintek 0,15-ös léptékekkel változnak.\n" +
            "Ennek a személynek a képességei és azok értékei a következők:\n"
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

    protected open fun putSkillsIntoPrompt(skills: List<ProfileItem>, prompt: String): String {
        var callPrompt = prompt
        for (skill in skills) {
            callPrompt += "${skill.ability.name}, leírás: ${skill.ability.description}, érték: ${skill.value}\n"
        }
        return callPrompt
    }

    open suspend fun generateFromSkills(skills: List<ProfileItem>, prompt: String = ""): String {
        var callPrompt = prompt.ifBlank { promptTemplate }
        callPrompt = putSkillsIntoPrompt(skills, callPrompt)
        return generateFromPrompt(callPrompt)
    }

    open suspend fun generateFromSkillsComparedToGroup(
        skills: List<ProfileItem>,
        groupSkills: List<ProfileItem>,
        groupName: String,
        prompt: String = ""
    ): String {
        var callPrompt = prompt.ifBlank { promptTemplateWithGroup }
        callPrompt = putSkillsIntoPrompt(skills, callPrompt)
        callPrompt += "A $groupName csoport átlagos értékei:\n"
        callPrompt = putSkillsIntoPrompt(groupSkills, callPrompt)
        return generateFromPrompt(callPrompt)
    }

    abstract suspend fun generateFromPrompt(prompt: String): String
}
