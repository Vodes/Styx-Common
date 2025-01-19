package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.compression.*

actual fun getNativeClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentEncoding) {
            gzip(1.0F)
            deflate(0.2F)
        }
        block()
    }
}