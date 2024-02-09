package moe.styx.common.extension

fun MutableList<String>.addIfNotExisting(s: String) {
    if (this.containsIgnoreCase(s))
        return

    this.add(s)
}

fun <T> MutableList<T>.replaceIfNotNull(toReplace: T?, replaceWith: T): MutableList<T> {
    if (toReplace != null) {
        val index = this.indexOf(toReplace)
        this[index] = replaceWith
    } else
        this.add(replaceWith)
    return this
}

fun List<String>.anyEquals(value: String, ignoreCase: Boolean = true, trim: Boolean = false): Boolean {
    for (element in this) {
        val trimmed = if (trim) element.trim() else element
        if (trimmed.equals(if (trim) value.trim() else value, ignoreCase))
            return true
    }
    return false
}

fun List<String>.containsIgnoreCase(value: String, trim: Boolean = true): Boolean {
    for (element in this) {
        val trimmed = if (trim) element.trim() else element
        if (trimmed.contains(if (trim) value.trim() else value, true))
            return true
    }
    return false
}