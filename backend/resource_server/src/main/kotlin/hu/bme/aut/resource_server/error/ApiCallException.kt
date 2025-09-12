package hu.bme.aut.resource_server.error

class ApiCallException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor() : super()
}
fun ApiCallException.removeUserFailed(): ApiCallException {
    return ApiCallException("Failed to remove user.")
}