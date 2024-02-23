package moe.styx.common

actual fun isWindows(): Boolean {
    return false
}

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.ANDROID