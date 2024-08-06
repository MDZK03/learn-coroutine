package com.lukaslechner.coroutineusecasesonandroid.playground.flow.concurrency

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow

suspend fun main() = coroutineScope {

    //BufferOverflow.SUSPEND or DROP_LATEST or DROP_OLDEST can be used
    val flow = flow {
        repeat(5) {
            println("Emitter:    Start Cooking Pancake $it")
            delay(100)
            println("Emitter:    Pancake $it ready!")
            emit(it)
        }
    }.buffer(1)
    
    flow.collect {
        println("Collector:  Start eating pancake $it")
        delay(300)
        println("Collector:  Finished eating pancake $it")
    }
}
