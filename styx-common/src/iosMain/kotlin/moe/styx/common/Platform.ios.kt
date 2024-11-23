package moe.styx.common

internal actual fun Platform.Companion.currentPlatform(): Platform = Platform.IOS
actual val isWindows: Boolean = false
actual val isDocker: Boolean = false