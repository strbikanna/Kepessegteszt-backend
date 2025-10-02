package hu.bme.aut.resource_server.recommendation.special_settings

import java.io.Serializable

data class SpecialSettingsDto(
    val distractionTypes: MutableSet<DistractionType> = mutableSetOf(),

    val minInterval: Long? = null,

    val maxInterval: Long? = null,

    val validMinutes: Long = 60
) : Serializable {
    constructor(specialSettings: Set<SpecialSettings>) : this(
        specialSettings.map { it.distractionType }.toMutableSet(),
        specialSettings.firstOrNull()?.minInterval,
        specialSettings.firstOrNull()?.maxInterval,
        specialSettings.firstOrNull()?.validMinutes ?: 60
    )

    fun toEntity(): Set<SpecialSettings> {
        return distractionTypes.map {
            SpecialSettings(
                distractionType = it,
                minInterval = minInterval,
                maxInterval = maxInterval,
                validMinutes = validMinutes
            )
        }.toSet()
    }
}