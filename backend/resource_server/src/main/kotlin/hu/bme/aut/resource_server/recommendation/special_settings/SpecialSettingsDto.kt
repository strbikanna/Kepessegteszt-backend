package hu.bme.aut.resource_server.recommendation.special_settings

data class SpecialSettingsDto(
    val distractionType: DistractionType = DistractionType.NONE,
) {
    constructor(specialSettings: SpecialSettings) : this(
        distractionType = specialSettings.distractionType
    )
}