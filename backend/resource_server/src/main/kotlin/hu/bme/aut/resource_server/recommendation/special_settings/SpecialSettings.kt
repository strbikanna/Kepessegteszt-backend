package hu.bme.aut.resource_server.recommendation.special_settings

import jakarta.persistence.*

@Embeddable
data class SpecialSettings(
    @Enumerated(EnumType.STRING)
    var distractionType: DistractionType = DistractionType.NONE,
) {


}