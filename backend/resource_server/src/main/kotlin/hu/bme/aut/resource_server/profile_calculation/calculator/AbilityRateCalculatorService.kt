package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import jep.JepException
import jep.SharedInterpreter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class AbilityRateCalculatorService(
    @Autowired private var userRepo: UserRepository
) {

    @Value("\${app.python.source-location}")
    private lateinit var filePath: String
    private val logger = LoggerFactory.getLogger(AbilityRateCalculatorService::class.java)
    fun calculateRates( abilityValues: List<List<Double?>>, resultValues: List<Double> ): List<Double>{
        logger.info("Executing python...")

        try{
            val interpreter = SharedInterpreter()
            interpreter.use {
                interpreter.exec("import sys")
                interpreter.exec("sys.path.append('$filePath')")
                interpreter.exec("from app import *")
                interpreter.set("matrix", abilityValues)
                interpreter.set("numbers", resultValues)
                interpreter.exec("ratios = calcRatio(matrix, numbers)")
                val answerWithRatios = interpreter.getValue("ratios")
                logger.info("Answer is: ${answerWithRatios}")
                // TODO test cast !!
                return answerWithRatios as List<Double>
            }
        }catch(ex: JepException){
            throw CalculationException("Exception in calculating ability contribution in game result: ${ex.message}")
        }catch(ex: RuntimeException){
            throw CalculationException("Exception occurred in processing calculation result: ${ex.message}")
        }
    }

    /**
     * Generates input for Python script
     * n*k size 2d array with n users and k abilities containing user abilities
     * and n size array with normalized results.
     * The two arrays contain the values for the users in same order.
     */
    @Transactional
    fun getAbilityValuesAndValuesFromResultsStructured(results: List<ResultForCalculationEntity>, abilities: List<AbilityEntity>): Pair<List<List<Double?>>, List<Double>>{
        val abilityValues = mutableListOf<List<Double?>>()
        val resultValues = mutableListOf<Double>()
        var user: UserEntity
        results.forEach { result ->
            if(result.normalizedResult != null) {
                user = userRepo.findByIdWithProfile(result.user.id!!).orElseThrow()
                val relevantProfileValues = user.profileFloat
                    .filter { abilities.contains(it.ability) }
                    .sortedBy { it.ability.code }
                    .map { it.abilityValue }
                abilityValues.add(relevantProfileValues)
                resultValues.add(result.normalizedResult!!)
            }
        }
        return Pair(abilityValues, resultValues)
    }
}