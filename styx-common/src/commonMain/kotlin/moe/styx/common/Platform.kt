package moe.styx.common

enum class Platform {
    JVM,
    ANDROID,
    IOS;

    companion object {
        val current: Platform
            get() = currentPlatform()
    }
}

internal expect fun Platform.Companion.currentPlatform(): Platform

expect val isWindows: Boolean
expect val isDocker: Boolean