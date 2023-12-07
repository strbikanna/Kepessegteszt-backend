package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import jep.JepException
import jep.NDArray
import jep.SharedInterpreter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.lang.RuntimeException

/**
 * Manages the neural network models for each game.
 * Creates new models.
 */
@Service
class ModelManager {

    @Value("\${app.python.models-location}")
    private lateinit var modelDir: String

    @Value("\${app.python.source-location}")
    private lateinit var scriptDir: String

    private val scriptName = "modellek"

    private val log: Logger = LoggerFactory.getLogger(ModelManager::class.java)

    /**
     * Calculates the estimated result of a game based on the given ability values.
     */
    fun getEstimationForResult(gameId: Int, abilityValues: List<Double?>): Double {
        if(!existsScript()){
            log.error("Script not found at $scriptDir/")
            throw CalculationException("Script not found at $scriptDir/")
        }
        try {
            val interpreter = SharedInterpreter()
            interpreter.use {
                interpreter.exec("import sys")
                interpreter.exec("sys.path.append('$scriptDir')")
                interpreter.exec("from $scriptName import *")
                interpreter.set("reszkepessegek", abilityValues)
                interpreter.set("path", "$modelDir$gameId")
                interpreter.exec("estimation = eredmenybecsles(path, reszkepessegek)")
                val estimatedResult = interpreter.getValue("estimation") as NDArray<FloatArray>
                log.info("Successful result estimation: $estimatedResult")
                return estimatedResult.data[0].toDouble()
            }
        } catch (ex: JepException) {
            log.error("Exception in result estimation: ${ex.message}")
            throw CalculationException("Exception in result estimation: ${ex.message}")
        }
    }

    /**
     * Creates a new neural network model for the given game.
     */
    suspend fun createNewModel(gameId: Int, abilityValues: List<List<Double?>>, resultValues: List<Double>) =
        CoroutineScope(Dispatchers.IO).launch {
            if(!existsScript()){
                log.error("Script not found at $scriptDir/")
                return@launch
            }
            try {
                val interpreter = SharedInterpreter()
                interpreter.use {
                    interpreter.exec("import sys")
                    interpreter.exec("sys.path.append('$scriptDir')")
                    interpreter.exec("from $scriptName import *")
                    interpreter.set("reszkepessegek", abilityValues)
                    interpreter.set("valaszok", resultValues)
                    interpreter.set("path", "$modelDir$gameId")
                    interpreter.exec("modelPath = eredmenybecsloModellTanitas(reszkepessegek, valaszok, path)")
                    val exportedModel = interpreter.getValue("modelPath")
                    log.info("Model created at $exportedModel")
                    return@launch
                }
            } catch (ex: JepException) {
                log.error("Exception in creating model of game: ${ex.message}")
                throw CalculationException("Exception in creating model of game: ${ex.message}")
            }
        }

    /**
     * Calculates the ability contributions for the result in the given game.
     */
    fun calculateRates(abilityValues: List<List<Double>>, resultValues: List<Double>): List<Double> {
        if(!existsScript()){
            log.error("Script not found at $scriptDir/")
            throw CalculationException("Script not found at $scriptDir/")
        }
        val primitive2dArray = mapToPrimitive2dArray(abilityValues)
        val primitiveArray = mapToPrimitiveArray(resultValues)
        try {
            val interpreter = SharedInterpreter()
            interpreter.use {
                interpreter.exec("import sys")
                interpreter.exec("sys.path.append('$scriptDir')")
                interpreter.exec("from $scriptName import *")
                interpreter.set("reszkepessegek", primitive2dArray)
                interpreter.set("valaszok", primitiveArray)
                interpreter.exec("ratios = reszkepesseg_fugges(reszkepessegek, valaszok)")
                val answerWithRatios = interpreter.getValue("ratios") as List<Double>
                log.info("Calculated ability contributions.")
                return answerWithRatios
            }
        } catch (ex: JepException) {
            throw CalculationException("Exception in calculating ability contribution in game result: ${ex.message}")
        } catch (ex: RuntimeException) {
            throw CalculationException("Exception occurred in processing calculation result: ${ex.message}")
        }
    }

    fun existsModel(gameId: Int): Boolean {
        return File("$modelDir$gameId").exists()
    }
    private fun existsScript(): Boolean {
        return File("$scriptDir/$scriptName.py").exists()
    }
    private fun mapToPrimitive2dArray(list: List<List<Double>>): NDArray<FloatArray> {
        val floatArray = FloatArray(list.size * list[0].size)
        list.forEachIndexed { outerindex, array ->
            array.forEachIndexed { index, value ->
                floatArray[outerindex * list[0].size + index] = value.toFloat()
            }
        }
        return NDArray(floatArray, list.size, list[0].size)
    }
    private fun mapToPrimitiveArray(list: List<Double>): NDArray<FloatArray> {
        return NDArray(list.map{it.toFloat()}.toFloatArray())
    }

}