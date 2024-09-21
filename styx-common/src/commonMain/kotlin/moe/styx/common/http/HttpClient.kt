package moe.styx.common.http

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.utils.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.io.readByteArray
import moe.styx.common.json
import moe.styx.common.util.Log
import moe.styx.common.util.SYSTEMFILES
import okio.Path
import kotlin.math.roundToInt

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
        install(ContentEncoding)
        install(HttpCookies)
    }
    return initializedHttpClient as HttpClient
}

sealed class DownloadResult {
    data object OK : DownloadResult()
    data object AbortExists : DownloadResult()
    data object FailedIO : DownloadResult()
    data object FailedRemote : DownloadResult()
    data object FailedUnknown : DownloadResult()
}

suspend fun downloadFileStream(url: String, outputPath: Path, progressCallback: (Int) -> Unit = {}): DownloadResult {
    if (SYSTEMFILES.exists(outputPath))
        return DownloadResult.AbortExists

    val result = runCatching {
        httpClient.prepareGet(url) {
            onDownload { bytesSentTotal, contentLength ->
                if (contentLength != null)
                    progressCallback((bytesSentTotal * 100f / contentLength).roundToInt())
            }
        }.execute { resp ->
            if (!resp.status.isSuccess()) {
                Log.e { "Failed to download file! (Response code ${resp.status.value})" }
                return@execute DownloadResult.FailedRemote
            }
            val channel: ByteReadChannel = resp.body()
            SYSTEMFILES.write(outputPath) {
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_HTTP_BUFFER_SIZE.toLong())
                    while (!packet.exhausted()) {
                        val bytes = packet.readByteArray()
                        write(bytes)
                    }
                }
            }
            return@execute DownloadResult.OK
        }
    }.onFailure {
        Log.e(exception = it) { "Failed to download file!" }
        return DownloadResult.FailedIO
    }
    return result.getOrNull() ?: DownloadResult.FailedUnknown
}

expect fun getNativeClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient