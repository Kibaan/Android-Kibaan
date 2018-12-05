package kibaan.android.http

import kibaan.android.ios.HTTPURLResponse
import kibaan.android.ios.TimeInterval
import kibaan.android.ios.components
import java.lang.Exception
import java.net.URLDecoder
import java.nio.charset.Charset


class HTTPConnectorMock: HTTPConnector {
    override var timeoutIntervalForRequest: TimeInterval = 30.0
    override var timeoutIntervalForResource: TimeInterval = 60.0
    override var isCancelled: Boolean = false
    var postData: ByteArray? = null

    override fun execute(request: Request, completionHandler: (ByteArray?, HTTPURLResponse?, Exception?) -> Unit) {
        postData = request.body
    }

    override fun cancel() {}

    fun getPostValue(key: String, encoding: Charset) : String {
        val data = postData ?: return ""
        val string = String(data, encoding)

        val keyValue = string.components("&")
                .map { it.components("=") }
                .first { it.first() == key }

        return URLDecoder.decode(keyValue.last(), "UTF-8")
    }
}
