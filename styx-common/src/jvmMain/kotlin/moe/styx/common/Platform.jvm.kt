package moe.styx.common

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.JVM
actual val isWindows by lazy {
    System.getProperty("os.name").contains("win", true)
}