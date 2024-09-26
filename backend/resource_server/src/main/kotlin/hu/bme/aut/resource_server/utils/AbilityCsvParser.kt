package hu.bme.aut.resource_server.utils

import org.jetbrains.annotations.Async.Schedule
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.nio.file.Paths

@Component
class AbilityCsvParser {

    //@Scheduled(fixedRate = 20000)
    fun parseCsvToSql(){
        val resource = object {}.javaClass.getResource("/abilities.csv")
        val csvFile = File(resource.path)
        val csvReader = FileReader(csvFile)
        val lines = csvReader.readLines().subList(1, 101)
        val sqlFile = File("C:\\Users\\Lenovo\\Anna\\Projects\\backend\\resource_server\\src\\main\\resources\\abilities.sql")
        val sqlWriter = sqlFile.bufferedWriter()
        sqlWriter.write("INSERT INTO float_profile_item (ability_id, user_id, ability_value) VALUES\n")
        for (line in lines){
            val parts = line.split(",")
            val userId = parts[0].toInt() + 100
            val abilityCodes = (1 until parts.size).map { getAbilityCodeByNumber(it -1) }
            val abilityValues = parts.subList(1, parts.size).map { it.toDouble() }
            for (i in abilityCodes.indices){
                sqlWriter.write("('${abilityCodes[i]}', $userId, ${abilityValues[i]}),\n")
            }
        }
        sqlWriter.close()
    }

    private fun getAbilityCodeByNumber(numberId: Int): String{
        return when(numberId){
            0 -> "Ga"
            1 -> "Gc"
            2 -> "Gf"
            3 -> "Glr"
            4 -> "Gq"
            5 -> "Grw"
            6 -> "Gsm"
            7 -> "Gv"
            8 -> "Gs"
            else -> "Gt"
        }
    }
}