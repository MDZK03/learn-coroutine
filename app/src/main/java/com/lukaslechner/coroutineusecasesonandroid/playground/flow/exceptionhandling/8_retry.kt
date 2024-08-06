package com.lukaslechner.coroutineusecasesonandroid.playground.flow.exceptionhandling

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
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
}.retryWhen { e, attempt -> //attempt starts at 0
    if (e is NetworkException && attempt < 2) {
        println("Retrying with exception: $e")
        delay(1000 * (attempt + 1))
        true
    } else false
    
    //if (e is NetWorkException || attempt < 2) 
    // retry forever with NetWorkException
    // or retry 2 times with any other exception
}

/* .retry(3) { e ->
    (e is NetworkException).also {
        println("Retrying with $e")
        delay(1000)
    }
}    */




class NetworkException(message: String) : Exception(message)
