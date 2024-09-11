package hu.bme.aut.resource_server.result

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Writer


internal object ExportCsvService{
    private val log = LoggerFactory.getLogger(ExportCsvService::class.java)
    fun exportCsv(results: List<ResultEntity>, writer: Writer) {
        val jsonMapper = jacksonObjectMapper()
        try {
            CSVPrinter(writer, CSVFormat.EXCEL).use { csvPrinter ->
                csvPrinter.printRecord("ID", "Timestamp", "User ID", "Game name", "Config", "Result", "Result passed")
                results.forEach { result ->
                    val configString = jsonMapper.writeValueAsString(result.config).replace("\"", "")
                    val resultString = jsonMapper.writeValueAsString(result.result).replace("\"", "")
                    csvPrinter.printRecord(
                        result.id,
                        result.timestamp,
                        result.user.id,
                        result.recommendedGame.game.name,
                        configString,
                        resultString,
                        result.result["passed"] as Boolean?
                    )
                }
            }
        } catch (e: IOException) {
            log.error("Error While writing CSV ", e)
        }
    }
}