package kibaan.http

import kibaan.ios.HTTPURLResponse
import kibaan.ios.TimeInterval
import java.lang.Exception

interface HTTPConnector {
    var timeoutIntervalForRequest: TimeInterval
    var timeoutIntervalForResource: TimeInterval
    val isCancelled: Boolean
    fun execute(request: Request, completionHandler: (ByteArray?, HTTPURLResponse?, Exception?) -> Unit)
    fun cancel()
}
