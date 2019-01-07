package kibaan.android.http

import kibaan.android.extension.substringFrom
import kibaan.android.ios.HTTPURLResponse
import java.nio.charset.Charset


open class HTTPTextTask(
    private val url: String,
    private val defaultEncoding: Charset = Charset.forName("UTF-8")
) : HTTPDataTask<String>() {

    override val requestURL: String
        get() = url

    override val httpMethod: String
        get() = "GET"

    override fun parseResponse(data: ByteArray, response: HTTPURLResponse): String {
        var stringEncoding = defaultEncoding
        val contentType = response.allHeaderFields["Content-Type"]
        val charsetName = contentType?.split(";")?.firstOrNull { param ->
            param.trim().toLowerCase().startsWith("charset=")
        }?.substringFrom(8)
        if (charsetName != null) {
            stringEncoding = Charset.forName(charsetName)
        }
        return String(bytes = data, charset = stringEncoding)
    }
}