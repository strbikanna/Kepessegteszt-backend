package hu.bme.aut.resource_server.utils

import hu.bme.aut.resource_server.authentication.AuthException
import hu.bme.aut.resource_server.profile_calculation.error.CalculationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.NoSuchElementException

@ControllerAdvice
class ErrorHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementError(exception: NoSuchElementException): ResponseEntity<String>{
        val message = exception.message ?: "Wrong request specification, nothing found."
        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentError(exception: IllegalArgumentException): ResponseEntity<String>{
        val message = exception.message ?: "Wrong request specification."
        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessError(exception: IllegalAccessException): ResponseEntity<String>{
        val message = exception.message ?: "No rights for this request."
        return ResponseEntity(message, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(CalculationException::class)
    fun handleCalculationException(exception: CalculationException): ResponseEntity<String>{
        val message = exception.message ?: "Error in calculation."
        return ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(CoroutineException::class)
    fun handleCoroutineException(exception: CoroutineException): ResponseEntity<String>{
        val message = exception.message ?: "Unknown error."
        return ResponseEntity(message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(exception: AuthException): ResponseEntity<String>{
        val message = exception.message ?: "Authentication error."
        return ResponseEntity(message, HttpStatus.FORBIDDEN)
    }
}