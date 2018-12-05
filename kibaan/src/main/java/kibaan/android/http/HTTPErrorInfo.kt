package kibaan.android.http

import kibaan.android.ios.HTTPURLResponse

data class HTTPErrorInfo(
    var error: Exception? = null,
    var response: HTTPURLResponse? = null,
    var data: ByteArray? = null)
