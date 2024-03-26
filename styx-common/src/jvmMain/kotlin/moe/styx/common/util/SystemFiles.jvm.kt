package moe.styx.common.util

import okio.FileSystem

actual val SYSTEMFILES: FileSystem
    get() = FileSystem.SYSTEM