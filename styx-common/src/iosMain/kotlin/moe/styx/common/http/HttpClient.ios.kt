package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.compression.*

actual fun getNativeClient(enableZstd: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Darwin) {
        install(ContentEncoding)
        block()
    }
}