package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.game_config.ConfigItem
import kotlin.math.max

object XPCalculator {

    fun calculateXP(configItems: Set<ConfigItem>, config: Map<String, Any>): Int {
        var xpGain = 0
        configItems.forEach { item ->
            val configValue = config[item.paramName]
            if (configValue != null && configValue is Int) {
                val difficulty =
                    if (item.hardestValue < item.easiestValue)
                        difficultyWithLowerHarder(configValue, item.hardestValue, item.easiestValue)
                    else
                        difficultyWithLowerEasier(configValue, item.hardestValue, item.easiestValue)
                xpGain += (difficulty * getMultiplicatorByDifficultyPercent(difficulty * 100)).toInt()
            }
        }
        return xpGain / configItems.size
    }

    private fun getMultiplicatorByDifficultyPercent(difficultyPercent: Double): Int {
        return when {
            difficultyPercent <= 50 -> 100
            difficultyPercent <= 60 -> 500
            else -> 1000
        }
    }

    private fun difficultyWithLowerHarder(actual: Int, hardest: Int, easiest: Int): Double {
        val interval = (easiest - hardest).toDouble()
        return max((easiest - actual).toDouble() / interval, 0.0)
    }

    private fun difficultyWithLowerEasier(actual: Int, hardest: Int, easiest: Int): Double {
        val interval = (hardest - easiest).toDouble()
        return max((actual - easiest).toDouble() / interval, 0.0)
    }
}