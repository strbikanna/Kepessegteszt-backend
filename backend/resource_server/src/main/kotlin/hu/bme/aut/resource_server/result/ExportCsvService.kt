package hu.bme.aut.resource_server.result

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.aut.resource_server.utils.BusinessCritical
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.Writer


internal object ExportCsvService{

    private val log = LoggerFactory.getLogger(ExportCsvService::class.java)

    @BusinessCritical
    fun exportCsv(results: List<ResultDetailsDto>, writer: Writer) {
        val jsonMapper = jacksonObjectMapper()
        try {
            CSVPrinter(writer, CSVFormat.EXCEL).use { csvPrinter ->
                csvPrinter.printRecord("ID", "Timestamp", "Username", "Game name", "Config", "Result", "Result passed")
                results.forEach { result ->
                    val configString = jsonMapper.writeValueAsString(result.config).replace("\"", "")
                    val resultString = jsonMapper.writeValueAsString(result.result).replace("\"", "")
                    csvPrinter.printRecord(
                        result.id,
                        result.timestamp,
                        result.username,
                        result.gameName,
                        configString,
                        resultString,
                        result.passed
                    )
                }
            }
        } catch (e: IOException) {
            log.error("Error While writing CSV ", e)
        }
    }
}