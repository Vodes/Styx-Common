package moe.styx.common

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = true
}

val prettyPrintJson = Json(json) {
    prettyPrint = true
}