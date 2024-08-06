package com.lukaslechner.coroutineusecasesonandroid.playground.flow.cancellation

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import java.math.BigInteger
import kotlin.coroutines.EmptyCoroutineContext

suspend fun main() {
    val scope = CoroutineScope(EmptyCoroutineContext)

    scope.launch {
        intFlow()
            .onCompletion { throwable ->
                if (throwable is CancellationException) {
                    println("Flow got cancelled.")
                }
            }
            .collect {
                println("Collected $it")

                if (it == 2) {
                    cancel()
                }
            }
    }.join()
}

private fun intFlow() = flow {
    emit(1)
    emit(2)

    // currentCoroutineContext().ensureActive()
    // even if coroutine is cancelled but calculateFactorial already run, it won't stop

    println("Start calculation")
    calculateFactorialOf(200)
    println("Calculation finished!")

    emit(3)
}

private suspend fun calculateFactorialOf(number: Int): BigInteger = coroutineScope {
    var factorial = BigInteger.ONE
    for (i in 1..number) {
        factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
        ensureActive()
    }
    factorial
}
