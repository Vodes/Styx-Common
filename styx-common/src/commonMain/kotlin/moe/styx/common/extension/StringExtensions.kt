package moe.styx.common.extension

import moe.styx.common.isWindows

infix fun String?.eqI(other: String?): Boolean {
    return this.equals(other, true)
}

fun String.capitalize(): String = lowercase().replaceFirstChar { it.uppercaseChar() }

fun String.replaceAll(replacement: String, vararg values: String, ignoreCase: Boolean = true): String {
    var new = this
    values.forEach {
        new = new.replace(it, replacement, ignoreCase)
    }
    return new
}

fun String?.equalsAny(vararg values: String, ignoreCase: Boolean = true, trim: Boolean = false): Boolean {
    if (this == null)
        return false
    for (value in values) {
        val thisString = if (trim) this.trim() else this
        if (thisString.equals(if (trim) value.trim() else value, ignoreCase))
            return true
    }
    return false
}

fun String.containsAny(vararg values: String, ignoreCase: Boolean = true): Boolean {
    for (value in values) {
        if (this.contains(value, ignoreCase))
            return true
    }
    return false
}

fun String.equalsAny(strings: List<String>, trim: Boolean = true, ignoreCase: Boolean = true): Boolean {
    return equalsAny(*strings.toTypedArray(), trim = trim, ignoreCase = ignoreCase)
}

/**
 * Removes characters from a string that might be invalid for a file on your system.
 *
 * ":" to " -" is really just a stylistic choice.
 *
 * For example: `Frieren: Beyond Journey’s End - S01E21`
 *
 * to `Frieren - Beyond Journey’s End - S01E21`
 */
fun String.toFileSystemCompliantName(): String {
    return if (isWindows()) {
        this.replace(":", " -").replaceAll("", "<", ">", "\"", "/", "\\", "|", "?", "*")
    } else {
        this.replace(":", " -").replace("/", "")
    }
}