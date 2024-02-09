package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.engine.java.*

actual fun getNativeClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Java) {
        block()
    }
}