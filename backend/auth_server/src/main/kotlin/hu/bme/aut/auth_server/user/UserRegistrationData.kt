package hu.bme.aut.auth_server.user

data class UserRegistrationData(
    val firstName: String,
    val lastName: String,
    val username: String,
    val password: String,
)
