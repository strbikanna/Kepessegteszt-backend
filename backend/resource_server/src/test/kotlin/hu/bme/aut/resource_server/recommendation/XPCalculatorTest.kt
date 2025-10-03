package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.game.game_config.ConfigItem
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class XPCalculatorTest {

    @Test
    fun `should calculate XP for one ConfigItem when hardest value is higher`() {
        val configItem = ConfigItem(
            paramName = "p1",
            easiestValue = 0,
            hardestValue = 10,
            initialValue = 1,
            maxAbilityEffect = 1.0,
            increment = 1,
            description = ""
        )

        var config = mapOf(Pair("p1", 2))
        var xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(60, xp)

        config = mapOf(Pair("p1", 3))
        xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(90, xp)

        config = mapOf(Pair("p1", 6))
        xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(600, xp)
    }

    @Test
    fun `should calculate XP for one ConfigItem when hardest value is lower`() {
        val configItem = ConfigItem(
            paramName = "p1",
            easiestValue = 1100,
            hardestValue = 100,
            initialValue = 200,
            maxAbilityEffect = 1.0,
            increment = 1,
            description = ""
        )

        var config = mapOf(Pair("p1", 1100))
        var xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(0, xp)

        config = mapOf(Pair("p1", 600))
        xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(500, xp)

        config = mapOf(Pair("p1", 300))
        xp = XPCalculator.calculateXP(setOf(configItem), config)
        assertEquals(1600, xp)
    }

    @Test
    fun `should calculate XP for multiple ConfigItems`() {
        var config = mapOf(Pair("p1", 1), Pair("p2", 3))
        var xp = XPCalculator.calculateXP(configItems, config)
        assertEquals(50, xp)

        config =mapOf(Pair("p1", 2), Pair("p2", 4))
        xp = XPCalculator.calculateXP(configItems, config)
        assertEquals(230, xp)

        config = mapOf(Pair("p1", 0), Pair("p2", 7))
        xp = XPCalculator.calculateXP(configItems, config)
        assertEquals(700, xp)
    }

    @Test
    fun `should not fail with incorrect values`() {
        var config = mapOf(Pair("p1", -1), Pair("p2", "aa"))
        var xp = XPCalculator.calculateXP(configItems, config)
        assertEquals(0, xp)

        config = mapOf(Pair("p1", 20))
        xp = XPCalculator.calculateXP(configItems, config)
        assertEquals(3000, xp)
    }

    private val configItems = setOf(
        ConfigItem(
            paramName = "p1",
            easiestValue = 0,
            hardestValue = 10,
            initialValue = 1,
            maxAbilityEffect = 1.0,
            increment = 1,
            description = ""
        ),
        ConfigItem(
            paramName = "p2",
            easiestValue = 0,
            hardestValue = 10,
            initialValue = 1,
            maxAbilityEffect = 1.0,
            increment = 1,
            description = ""
        ),
    )
}