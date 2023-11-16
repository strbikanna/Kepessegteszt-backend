package hu.bme.aut.resource_server.profile_calculation.error

import java.lang.RuntimeException

class ProfileUpdateException: RuntimeException {
    constructor(message: String): super(message)
    constructor(): super()
}