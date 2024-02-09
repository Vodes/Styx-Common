package moe.styx.common

actual val current: Platform
    get() = Platform.JVM

actual fun isWindows(): Boolean {
    return System.getProperty("os.name").contains("win", true)
}