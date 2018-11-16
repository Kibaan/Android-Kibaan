package jp.co.altonotes.android.kibaan.http

import jp.co.altonotes.android.kibaan.ios.HTTPURLResponse

data class HTTPErrorInfo(
    var error: Exception? = null,
    var response: HTTPURLResponse? = null,
    var data: ByteArray? = null)
