package moe.styx.common

actual fun isWindows(): Boolean {
    return System.getProperty("os.name").contains("win", true)
}

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.JVM