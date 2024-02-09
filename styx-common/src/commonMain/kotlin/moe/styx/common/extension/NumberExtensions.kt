package moe.styx.common.extension

fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean() = this > 0

fun Int.padString(length: Int = 2): String {
    return this.toString().padStart(length, '0')
}

