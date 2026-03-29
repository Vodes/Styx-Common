package moe.styx.common.extension

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

fun currentUnixSeconds(): Long {
    return Clock.System.now().epochSeconds
}

fun Long.toDateString(): String {
    val instant = Instant.fromEpochSeconds(this)
    val datetime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${datetime.year}-${datetime.month.number.padString()}-${datetime.day.padString()} "
}

fun LocalDateTime.formattedStr(): String {
    return "${this.year}-${month.number.padString()}-${day.padString()} " +
            "${this.hour.padString()}:${this.minute.padString()}:${this.second.padString()}"
}

fun LocalDateTime.formattedStrFile(): String {
    return "${this.year}-${month.number.padString()}-${day.padString()} " +
            "${this.hour.padString()}-${this.minute.padString()}-${this.second.padString()}"
}