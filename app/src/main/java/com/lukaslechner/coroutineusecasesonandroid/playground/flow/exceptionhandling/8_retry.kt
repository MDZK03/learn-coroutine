package com.lukaslechner.coroutineusecasesonandroid.playground.flow.exceptionhandling

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch

suspend fun main(): Unit = coroutineScope {

    launch {
        stocksFlow()
            .catch { throwable ->
                println("Handle exception in catch() operator $throwable")
            }
            .collect { stockData ->
                println("Collected $stockData")
            }
    }
}

private fun stocksFlow(): Flow<String> = flow {

    repeat(5) { index ->

        delay(1000)

        val check = index < 4
        if (check) {
            emit("New Stock data")
        } else {
            throw NetworkException("Network Request Failed!")
        }
    }
}.retryWhen { cause, attempt -> //attempt starts at 0
    if (cause is NetworkException) {
        println("Retrying with exception: $cause")
        delay(1000 * (attempt+1))
        attempt < 2 //will retry 2 times
    } else false

}

class NetworkException(message: String) : Exception(message)
