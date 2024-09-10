package hu.bme.aut.resource_server.utils

import kotlinx.coroutines.CoroutineExceptionHandler

val defaultCoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    println(throwable)
    throw CoroutineException(throwable)
}