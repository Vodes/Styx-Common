package moe.styx.common

actual val current: Platform
    get() = Platform.IOS

actual fun isWindows(): Boolean {
    return false
}