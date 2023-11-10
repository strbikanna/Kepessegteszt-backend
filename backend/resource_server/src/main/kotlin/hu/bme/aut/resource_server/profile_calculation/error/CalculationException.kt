package hu.bme.aut.resource_server.profile_calculation.error

class CalculationException(
    override val message: String
): RuntimeException(message) {

}