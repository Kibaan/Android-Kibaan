package kibaan.android.http

import kibaan.android.ios.HTTPURLResponse
import kibaan.android.ios.TimeInterval
import kibaan.android.ios.first
import okhttp3.*
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.TimeUnit


class HTTPConnectorImpl: HTTPConnector {

    companion object {
        val httpClient = OkHttpClient()
    }

    var httpCall: Call? = null
    override var timeoutIntervalForRequest: TimeInterval = 30.0
    override var timeoutIntervalForResource: TimeInterval = 60.0
    override var isCancelled: Boolean = false

    override fun execute(request: Request, completionHandler: (ByteArray?, HTTPURLResponse?, Exception?) -> Unit) {
        isCancelled = false

        val client = httpClient.newBuilder()
                .connectTimeout(timeoutIntervalForRequest.toLong(), TimeUnit.SECONDS)
                .readTimeout(timeoutIntervalForResource.toLong(), TimeUnit.SECONDS)
                .build()


        // リクエスト作成
        val requestBuilder = okhttp3.Request.Builder()
                .url(request.url)
                .cacheControl(CacheControl.Builder().noCache().noStore().build())

        if (request.httpMethod == "POST") {
            val headerContentType = request.headers.first {
                it.key.toLowerCase() == "content-type"
            }?.value
            val contentType = headerContentType ?: "application/octet-stream"
            val body = RequestBody.create(MediaType.parse(contentType), request.body)
            requestBuilder.post(body)
        } else {
            requestBuilder.get()
        }

        // ヘッダー付与
        for ((key, value) in request.headers) {
            requestBuilder.addHeader(key, value)
        }

        val httpCall = client.newCall(requestBuilder.build())
        this.httpCall = httpCall

        httpCall.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.bytes()
                val responseHeaders = response.headers()
                val allHeaderFields = mutableMapOf<String, String>()
                responseHeaders.names().forEach {
                    val headerValue = responseHeaders[it] ?: return@forEach
                    allHeaderFields[it] = headerValue
                }
                val urlResponse = HTTPURLResponse(response.code(), allHeaderFields)

                completionHandler(body, urlResponse, null)
                this@HTTPConnectorImpl.httpCall = null
            }

            override fun onFailure(call: Call, e: IOException) {
                completionHandler(null, null, e)
                this@HTTPConnectorImpl.httpCall = null
            }
        })
    }

    override fun cancel() {
        httpCall?.cancel()
        isCancelled = true
    }
}
