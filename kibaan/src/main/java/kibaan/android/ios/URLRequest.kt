package kibaan.android.ios

import android.webkit.WebSettings

class URLRequest(var url: String, var cachePolicy: CachePolicy, var timeoutInterval: TimeInterval) {

    enum class CachePolicy(override val rawValue: Int) : IntEnum {
        reloadIgonoringCacheData(WebSettings.LOAD_NO_CACHE),
        default(WebSettings.LOAD_DEFAULT),
        loadCacheOnly(WebSettings.LOAD_CACHE_ONLY)
    }

    var httpBody: ByteArray? = null
    var httpMethod: String = "GET"
}