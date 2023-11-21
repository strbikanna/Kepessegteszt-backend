package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import jep.JepException
import jep.SharedInterpreter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File

/**
 * Manages the neural network models for each game. Stores and reads JSON files containing the models.
 * Creates new models.
 */
@Service
class ModelManager {

    @Value("\${app.python.models-location}")
    private lateinit var modelDir: String

    @Value("\${app.python.models-location}")
    private lateinit var scriptDir: String

        fun getEstimationForResult(gameId: Int, abilityValues: List<Double?>): Double{
            TODO()
        }

        suspend fun createNewModel(gameId: Int, abilityValues: List<List<Double?>>, resultValues: List<Double>) = CoroutineScope(Dispatchers.IO).launch{
            try{
                val interpreter = SharedInterpreter()
                interpreter.use {
                    interpreter.exec("import sys")
                    interpreter.exec("sys.path.append('$scriptDir')")
                    interpreter.exec("from app import *")
                    interpreter.set("matrix", abilityValues)
                    interpreter.set("numbers", resultValues)
                    interpreter.exec("model = calcRatio(matrix, numbers)")
                    val exportedModel = interpreter.getValue("model")
                    saveModel(gameId, exportedModel as String)
                }
            }catch(ex: JepException){
                throw CalculationException("Exception in creating model of game: ${ex.message}")
            }
        }
        fun existsModel(gameId: Int): Boolean{
            return File("$modelDir$gameId.json").exists()
        }
        private fun loadModel(gameId: Int): String{
            val file = File("$modelDir$gameId.json")
            return file.readText()
        }
        private fun saveModel(gameId: Int, model: String){
            val file = File("$modelDir$gameId.json")
            file.writeText(model)
        }

}