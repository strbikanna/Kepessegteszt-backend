package hu.bme.aut.resource_server.profile_calculation.calculator

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Helper class for calculation of mean and standard deviation
 */
object CalculationHelper {

        fun calculateMean(dataSet: List<Double> ): Double{
            if(dataSet.isEmpty()) return 0.0
            val sum = dataSet.foldRight(0.0){item, acc -> acc + item}
            return sum / dataSet.size
        }

        fun calculateStdDeviation(dataSet: List<Double> , mean: Double): Double{
            if(dataSet.isEmpty() || mean == 0.0) return 0.0
            val sumOfDifferenceSquare = dataSet.foldRight(0.0){item, acc -> acc + (item - mean).pow(2) }
            return sqrt(sumOfDifferenceSquare / dataSet.size )
        }

}