package moe.styx.common

import kotlinx.serialization.json.Json
import net.peanuuutz.tomlkt.Toml

val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = true
}

val toml = Toml {
    explicitNulls = true
    ignoreUnknownKeys = true
}

val prettyPrintJson = Json(json) {
    prettyPrint = true
}