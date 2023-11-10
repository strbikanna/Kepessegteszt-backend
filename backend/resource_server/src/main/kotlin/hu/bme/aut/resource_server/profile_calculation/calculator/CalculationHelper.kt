package hu.bme.aut.resource_server.profile_calculation.calculator

import kotlin.math.pow
import kotlin.math.sqrt

object CalculationHelper {

        fun calculateMean(dataSet: List<Double> ): Double{
            val sum = dataSet.foldRight(0.0){item, acc -> acc + item}
            return sum / dataSet.size
        }

        fun calculateStdDeviation(dataSet: List<Double> , mean: Double): Double{
            val sumOfDifferenceSquare = dataSet.foldRight(0.0){item, acc -> acc + (item - mean).pow(2) }
            return sqrt(sumOfDifferenceSquare / mean )
        }

}