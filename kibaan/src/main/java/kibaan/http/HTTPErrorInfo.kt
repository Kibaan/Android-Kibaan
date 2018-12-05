package kibaan.http

import kibaan.ios.HTTPURLResponse

data class HTTPErrorInfo(
    var error: Exception? = null,
    var response: HTTPURLResponse? = null,
    var data: ByteArray? = null)
