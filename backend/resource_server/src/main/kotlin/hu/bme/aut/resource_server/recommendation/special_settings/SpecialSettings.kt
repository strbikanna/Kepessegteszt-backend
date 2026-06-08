package hu.bme.aut.resource_server.recommendation.special_settings

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Embeddable
data class SpecialSettings(
    @Enumerated(EnumType.STRING)
    var distractionType: DistractionType = DistractionType.VISUAL,

    var minInterval: Long? = null,

    var maxInterval: Long? = null,

    @CreationTimestamp
    var creationTimestamp: LocalDateTime = LocalDateTime.now(),

    val validMinutes: Long = 60
) {

    fun isValid(): Boolean{
        return creationTimestamp.plusMinutes(validMinutes) >= LocalDateTime.now()
    }

}