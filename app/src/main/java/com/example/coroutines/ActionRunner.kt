package com.example.coroutines

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class ActionRunner {

    suspend fun runAsync() = suspendCoroutine<Int> { cont ->
        run { randomValue ->
            cont.resume(randomValue)
        }
    }

    fun run(action: (Int) -> Unit) {
        action(Random.nextInt())
    }
}