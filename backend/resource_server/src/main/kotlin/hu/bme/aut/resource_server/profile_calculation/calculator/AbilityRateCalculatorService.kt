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

@Service
class AbilityRateCalculatorService(
    @Autowired private var userRepo: UserRepository
) {

    @Value("\${app.python.source-location}")
    private lateinit var filePath: String
    private val logger = LoggerFactory.getLogger(AbilityRateCalculatorService::class.java)
    fun calculateRates( abilityValues: List<List<Double>>, resultValues: List<Double> ){

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
            }
        }catch(ex: JepException){
            throw CalculationException("Exception in calculating ability contribution in game result: ${ex.message}")
        }
    }

    @Transactional
    fun getAbilitiesForResults(results: List<ResultForCalculationEntity>, abilities: List<AbilityEntity>){
        val abilityValues = mutableListOf<List<Double?>>()
        val resultValues = mutableListOf<Double>()
        var user: UserEntity
        results.forEach { result ->
            if(result.normalizedResult != null) {
                user = userRepo.findById(result.user.id!!).orElseThrow()
                val relevantProfileValues = mutableListOf<Double?>()
                abilities.forEach { ability ->
                    val profileAbilityValue = user.profileFloat.find { it.ability.code == ability.code }?.abilityValue
                    relevantProfileValues.add(profileAbilityValue)
                }
                abilityValues.add(relevantProfileValues)
                resultValues.add(result.normalizedResult!!)
            }
        }
    }
}