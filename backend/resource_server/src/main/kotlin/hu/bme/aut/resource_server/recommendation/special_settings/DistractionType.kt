package hu.bme.aut.resource_server.recommendation.special_settings

enum class DistractionType {
    NOTIFICATION,
    VISUAL,
    SOUND,
    BLACKSCREEN,
    PAVLOVIAN,
    VIBRATION
}

const val  DISTRACTION_CONFIG_KEY = "distraction"
const val  XP_CONFIG_KEY = "xp_increment"
