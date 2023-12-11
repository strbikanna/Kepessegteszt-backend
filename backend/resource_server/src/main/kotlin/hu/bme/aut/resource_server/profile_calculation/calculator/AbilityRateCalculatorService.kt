package hu.bme.aut.resource_server.profile_calculation.calculator

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationEntity
import hu.bme.aut.resource_server.recommendation.ModelManager
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Helper class for multi-ability games to
 * determine the affect of each ability on the result.
 */
@Service
class AbilityRateCalculatorService(
    @Autowired private var userRepo: UserRepository,
    @Autowired private var modelManager: ModelManager
) {
    /**
     * Calculates the contribution of each ability to the result.
     * @param abilityValues: n*k size 2d array with n users and k abilities containing user ability values
     * @param resultValues: n size array with normalized results.
     */
    fun calculateRates(abilityValues: List<List<Double>>, resultValues: List<Double>): List<Double> {
        return modelManager.calculateRates(abilityValues, resultValues)
    }

    /**
     * Generates input for Python script
     * n*k size 2d array with n users and k abilities containing user abilities
     * and n size array with normalized results.
     * The two arrays contain the values for the users in same order.
     */
    @Transactional
    fun getAbilityValuesAndValuesFromResultsStructured(
        results: List<ResultForCalculationEntity>,
        abilities: List<AbilityEntity>
    ): Pair<List<List<Double>>, List<Double>> {
        val abilityValues = mutableListOf<List<Double>>()
        val resultValues = mutableListOf<Double>()
        var user: UserEntity
        results.forEach { result ->
            if (result.normalizedResult != null) {
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