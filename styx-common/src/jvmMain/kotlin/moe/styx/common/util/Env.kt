package moe.styx.common.util

fun getEnvString(env: String, default: String): String {
    return runCatching { System.getenv(env) }.getOrNull()?.let { it.ifBlank { null } } ?: default
}

fun getEnvBool(env: String, default: Boolean): Boolean {
    return runCatching { System.getenv(env) }.getOrNull()?.let { it.ifBlank { null } }?.toBoolean() ?: default
}

fun getEnvDouble(env: String, default: Double): Double {
    return runCatching { System.getenv(env) }.getOrNull()?.let { it.ifBlank { null } }?.toDoubleOrNull() ?: default
}

fun getEnvInt(env: String, default: Int): Int {
    return runCatching { System.getenv(env) }.getOrNull()?.let { it.ifBlank { null } }?.toIntOrNull() ?: default
}

fun getEnvFloat(env: String, default: Float): Float {
    return runCatching { System.getenv(env) }.getOrNull()?.let { it.ifBlank { null } }?.toFloatOrNull() ?: default
}