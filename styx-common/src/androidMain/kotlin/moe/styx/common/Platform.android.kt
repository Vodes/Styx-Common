package moe.styx.common

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.ANDROID
actual val isWindows: Boolean = false
actual val isDocker: Boolean = false