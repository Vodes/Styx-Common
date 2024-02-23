package moe.styx.common.util

import kotlinx.coroutines.*

fun launchThreaded(run: suspend CoroutineScope.() -> Unit): Job {
    val scope = CoroutineScope(Dispatchers.IO)
    return scope.launch {
        run()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun launchGlobal(run: suspend CoroutineScope.() -> Unit): Job {
    return GlobalScope.launch {
        run()
    }
}

suspend fun awaitAll(vararg jobs: Job) {
    jobs.asList().joinAll()
}