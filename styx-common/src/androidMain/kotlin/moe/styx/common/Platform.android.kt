package moe.styx.common

actual val current: Platform
    get() = Platform.ANDROID

actual fun isWindows(): Boolean {
    return false
}