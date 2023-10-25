package hu.bme.aut.resource_server.utils

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
    fun handleIllegalArgumentError(exception: NoSuchElementException): ResponseEntity<String>{
        val message = exception.message ?: "Wrong request specification."
        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccessError(exception: NoSuchElementException): ResponseEntity<String>{
        val message = exception.message ?: "No rights for this request."
        return ResponseEntity(message, HttpStatus.FORBIDDEN)
    }
}