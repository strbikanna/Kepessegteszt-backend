package hu.bme.aut.resource_server.profile_calculation.error

import java.lang.RuntimeException

/**
 * Exception thrown when the profile update fails.
 */
class ProfileUpdateException: RuntimeException {
    constructor(message: String): super(message)
    constructor(): super()
}