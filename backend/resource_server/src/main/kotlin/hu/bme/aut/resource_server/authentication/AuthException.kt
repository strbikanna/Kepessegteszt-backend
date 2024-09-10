package hu.bme.aut.resource_server.authentication

class AuthException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor() : super()
}

fun AuthException.notContact(): AuthException {
    return AuthException("User is not a contact.")
}