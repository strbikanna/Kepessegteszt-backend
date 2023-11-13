package hu.bme.aut.resource_server.recommendation

import hu.bme.aut.resource_server.profile_calculation.data.ResultForCalculationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AutoRecommendationService(
    @Autowired private var resultRepository: ResultForCalculationRepository,
) {
}