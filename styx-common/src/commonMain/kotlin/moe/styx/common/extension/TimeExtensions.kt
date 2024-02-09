package moe.styx.common.extension

import kotlinx.datetime.*

fun currentUnixSeconds(): Long {
    return Clock.System.now().epochSeconds
}

fun Long.toDateString(): String {
    val instant = Instant.fromEpochSeconds(this)
    val datetime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${datetime.year}-${datetime.monthNumber.padString()}-${datetime.dayOfMonth.padString()} "
}

fun LocalDateTime.formattedStr(): String {
    return "${this.year}-${this.monthNumber.padString()}-${this.dayOfMonth.padString()} " +
            "${this.hour.padString()}:${this.minute.padString()}:${this.second.padString()}"
}

fun LocalDateTime.formattedStrFile(): String {
    return "${this.year}-${this.monthNumber.padString()}-${this.dayOfMonth.padString()} " +
            "${this.hour.padString()}-${this.minute.padString()}-${this.second.padString()}"
}