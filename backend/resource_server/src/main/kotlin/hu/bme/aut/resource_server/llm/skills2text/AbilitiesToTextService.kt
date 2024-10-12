package hu.bme.aut.resource_server.llm.skills2text

import hu.bme.aut.resource_server.profile.ProfileItem

abstract class AbilitiesToTextService(
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

    protected open fun putAbilitiesIntoPrompt(abilities: List<ProfileItem>, prompt: String): String {
        var callPrompt = prompt
        for (ability in abilities) {
            callPrompt += "${ability.ability.name}, leírás: ${ability.ability.description}, érték: ${ability.value}\n"
        }
        return callPrompt
    }

    open suspend fun generateFromAbilities(abilities: List<ProfileItem>, prompt: String = ""): String {
        var callPrompt = prompt.ifBlank { promptTemplate }
        callPrompt = putAbilitiesIntoPrompt(abilities, callPrompt)
        return generateFromPrompt(callPrompt)
    }

    open suspend fun generateFromAbilitiesComparedToGroup(
        abilities: List<ProfileItem>,
        groupAbilities: List<ProfileItem>,
        groupName: String,
        prompt: String = ""
    ): String {
        var callPrompt = prompt.ifBlank { promptTemplateWithGroup }
        callPrompt = putAbilitiesIntoPrompt(abilities, callPrompt)
        callPrompt += "A $groupName csoport átlagos értékei:\n"
        callPrompt = putAbilitiesIntoPrompt(groupAbilities, callPrompt)
        return generateFromPrompt(callPrompt)
    }

    abstract suspend fun generateFromPrompt(prompt: String): String
}
