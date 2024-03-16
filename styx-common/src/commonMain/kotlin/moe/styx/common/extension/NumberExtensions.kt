package moe.styx.common.extension

import kotlin.math.floor
import kotlin.math.roundToInt

fun Boolean.toInt() = if (this) 1 else 0
fun Int.toBoolean() = this > 0

fun Int.padString(length: Int = 2): String {
    return this.toString().padStart(length, '0')
}

fun Long.readableSize(useBinary: Boolean = false): String {
    val units = if (useBinary) listOf("B", "KiB", "MiB", "GiB", "TiB") else listOf("B", "KB", "MB", "GB", "TB")
    val divisor = if (useBinary) 1024 else 1000
    var steps = 0
    var current = this.toDouble()
    while (floor((current / divisor)) > 0) {
        current = (current / divisor)
        steps++;
    }
    return "${if (steps > 2) current.roundToDecimals(1) else current.roundToDecimals(2)} ${units[steps]}"
}

fun Double.roundToDecimals(decimals: Int): Double {
    var dotAt = 1
    repeat(decimals) { dotAt *= 10 }
    val roundedValue = (this * dotAt).roundToInt()
    return (roundedValue / dotAt) + (roundedValue % dotAt).toDouble() / dotAt
}
