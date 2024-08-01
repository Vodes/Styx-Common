package moe.styx.common.util

import android.util.Log as AndroidLog

private object LogImpl : ALog() {
    override fun printMsg(message: String, prefix: String, source: String?, exception: Throwable?, printStack: Boolean) {
        when (prefix) {
            "I" -> AndroidLog.i(source ?: "Styx", message, exception)
            "D" -> AndroidLog.d(source ?: "Styx", message, exception)
            "W" -> AndroidLog.w(source ?: "Styx", message, exception)
            else -> AndroidLog.e(source ?: "Styx", message, exception)
        }

        if (printStack)
            exception?.printStackTrace()
    }
}

actual val Log: ALog
    get() = LogImpl