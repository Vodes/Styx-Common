package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import moe.styx.common.json

private var initializedHttpClient: HttpClient? = null

val httpClient: HttpClient
    get() = initializedHttpClient ?: getHttpClient()

fun getHttpClient(userAgent: String? = null): HttpClient {
    initializedHttpClient = getNativeClient {
        if (!userAgent.isNullOrBlank())
            install(UserAgent) {
                agent = userAgent
            }

        install(ContentNegotiation) {
            json
        }
    }
    return initializedHttpClient as HttpClient
}

expect fun getNativeClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient