package com.lukaslechner.coroutineusecasesonandroid.playground.fundamentals

import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.delay

fun main() = runBlocking {
    println("main starts")
    joinAll(
        async { coroutine(1, 500) },
        async { coroutine(2, 100) }
    )
    println("main ends")
}

suspend fun coroutine(num: Int, delay: Long){
    println("Coroutine $num starts work")
    delay(delay)
    println("Coroutine $num has done")
}
