package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.compression.*
import pw.vodes.zstd.zstd

actual fun getNativeClient(enableZstd: Boolean, block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentEncoding) {
            if (enableZstd) {
                zstd(1.0F)
                gzip(0.5F)
                deflate(0.2F)
            } else {
                gzip(1.0F)
                deflate(0.5F)
            }
        }
        block()
    }
}