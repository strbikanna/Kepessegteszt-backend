package hu.bme.aut.resource_server.error

/**
 * Exception thrown when a calculation error occurs.
 */
class CalculationException(
    override val message: String
): RuntimeException(message) {}