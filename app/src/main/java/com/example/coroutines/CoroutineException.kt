package com.example.coroutines

import kotlinx.coroutines.*

fun main() = runBlocking {
    val handler = CoroutineExceptionHandler { _, exception ->
        log("exception thrown $exception")
    }
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + handler)

    val parentJob = scope.launch {

        val jobA = launch {
            val resultA = getResult(1)
            log("resultA : $resultA")
        }
        jobA.invokeOnCompletion { throwable ->
            if (throwable != null) {
                log("error resultA: $throwable")
            }
        }

        val jobB = launch {
            val resultB = getResult(2)
            log("resultB : $resultB")
        }
        jobB.invokeOnCompletion { throwable ->
            if (throwable != null) {
                log("error resultB: $throwable")
            }
        }

        val jobC = launch {
            val resultC = getResult(3)
            log("resultC : $resultC")
        }
        jobC.invokeOnCompletion { throwable ->
            if (throwable != null) {
                log("error resultC: $throwable")
            }
        }
    }

    parentJob.invokeOnCompletion { throwable ->
        if (throwable != null) {
            log("parent job failed")
        } else {
            log("parent job success")
        }
    }
    parentJob.join()
}

private suspend fun getResult(number: Int): Int {
    delay(number * 500L)

    if (number == 2) {
        error("error getting result from $number")
//            throw CancellationException("error getting result from $number")
    }

    return number * 2
}

private fun log(message: String) {
    println("CoroutineException $message")
}