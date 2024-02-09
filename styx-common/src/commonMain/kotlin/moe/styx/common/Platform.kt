package moe.styx.common

enum class Platform {
    JVM,
    ANDROID,
    IOS
}

expect val current: Platform

expect fun isWindows(): Boolean