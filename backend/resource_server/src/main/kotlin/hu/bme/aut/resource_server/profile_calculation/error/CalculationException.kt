package hu.bme.aut.resource_server.profile_calculation.error

/**
 * Exception thrown when a calculation error occurs.
 */
class CalculationException(
    override val message: String
): RuntimeException(message) {}