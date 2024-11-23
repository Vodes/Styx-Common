package moe.styx.common

import java.io.File

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.JVM

actual val isWindows by lazy {
    System.getProperty("os.name").contains("win", true)
}

actual val isDocker by lazy {
    File("/.dockerenv").exists()
}