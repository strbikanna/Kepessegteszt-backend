package hu.bme.aut.resource_server.llm.abilities2text

import dev.langchain4j.data.message.SystemMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatLanguageModel
import hu.bme.aut.resource_server.profile.ProfileItem

abstract class AbilitiesToTextService {
    protected open val promptTemplate = "Alakítsd át a következő Cattell–Horn–Carroll (CHC) elmélet képességértékeket " +
            "egy rövid szöveges összefoglalóvá a játékos kognitív adottságairól, " +
            "úgy hogy ne a számértékeket add vissza, hanem fogalmazz mondatokat! " +
            "Minden képesség átlagos értéke 1,0. A szintek 0,15-ös léptékekkel változnak. Legyen változatos a szöveg megfogalmazása, " +
            "ne csak az átlaghoz hasonlításról szóljon, hanem személyre szabott legyen!\n" +
            "Ennek a személynek a képességei és azok értékei a következők:\n"
    protected open val promptTemplateWithGroup = "Alakítsd át a következős Cattell–Horn–Carroll (CHC) elmélet képességértékeket " +
            "egy rövid összefoglalóvá a játékos kognitív adottságairól az adott csoporthoz képest!\n" +
            "A szintek 0,15-ös léptékekkel változnak.\n" +
            "Ennek a személynek a képességei és azok értékei a következők:\n"
    protected open val systemMessage: SystemMessage = SystemMessage
        .from("Egy gyerek képességeit a szűlőnek értékekről értelmezhető szöveggé alakító asszisztens vagy!")


    protected abstract val model: ChatLanguageModel
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

    open suspend fun generateFromPrompt(prompt: String): String {
        val messages = listOf(
            systemMessage,
            UserMessage.from(prompt)
        )
        val response = model.generate(messages)
        val result = response.content().text()

        log(prompt = prompt, response = result)
        return result
    }
}
