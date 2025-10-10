package hu.bme.aut.resource_server.llm.tools

import dev.langchain4j.agent.tool.Tool
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class KnowledgeBaseTool {

    private val knowledgeBasePath: String = "knowledgebase"
    private val log = LoggerFactory.getLogger(KnowledgeBaseTool::class.java)

    @Tool("Provides an example evaluation of a student with profile values.")
    fun getExampleEvaluation(): String {
        log.info("Fetching example evaluation from knowledge base.")
        val fullPath = "$knowledgeBasePath/example-evaluation.txt"
        return readTextFile(fullPath)
    }

    @Tool("Provides descriptions about the meaning of the abilities in the CHC model. Helps understanding the ability.")
    fun getChcAbilityDescriptions(): String {
        log.info("Fetching CHC ability descriptions from knowledge base.")
        val fullPath = "$knowledgeBasePath/chc-ability-descriptions.txt"
        return readTextFile(fullPath)
    }

    @Tool("Provides interpretations of ability values in the CHC model. Helps understanding what the specific numeric values mean. " +
            "Can be used to interpret the numeric values of the abilities.")
    fun getChcAbilityValueInterpretations(): String {
        log.info("Fetching CHC ability value interpretations from knowledge base.")
        val fullPath = "$knowledgeBasePath/ability-value-interpretation.txt"
        return readTextFile(fullPath)
    }


    @Tool("Provides example text evaluations to ability levels in CSV format. " +
            "Ability levels are considered low/medium/high.")
    fun getAbilityLevelExamples(): String {
        log.info("Fetching ability level examples from knowledge base.")
        val fullPath = "$knowledgeBasePath/CHC-ability-level-examples.csv"
        val file = File(fullPath)
        if (!file.exists()) {
            return "Error: File does not exist at path $fullPath"
        }

        val csvContent = StringBuilder()
        CSVParser.parse(file, Charsets.UTF_8, CSVFormat.DEFAULT).use { parser ->
            for (record in parser) {
                csvContent.append(record.joinToString(",")).append("\n")
            }
        }
        return csvContent.toString()
    }

    private fun readTextFile(filePath: String): String {
        val path = Path.of(filePath)
        if (!Files.exists(path)) {
            return "Error: File does not exist at path $filePath"
        }
        return Files.readString(path)
    }
}