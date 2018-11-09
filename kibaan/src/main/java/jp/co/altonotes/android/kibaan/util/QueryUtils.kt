package jp.co.altonotes.android.kibaan.util

import jp.co.altonotes.android.kibaan.ios.components
import jp.co.altonotes.android.kibaan.ios.joined
import jp.co.altonotes.android.kibaan.ios.removingPercentEncoding
import jp.co.altonotes.android.kibaan.ios.safeGet
import jp.co.altonotes.android.kibaan.valueobject.KeyValue
import java.net.URLEncoder

/**
 *
 * Created by Yamamoto Keita on 2018/01/17.
 */
object QueryUtils {


    /**
     * Key-Valueのリストからクエリ文字列を作成する
     */
    fun makeQueryString(items: List<KeyValue>, encoder: ((String) -> (String))? = null): String? {
        if (items.isNotEmpty()) {
            return items.map {
                val encoder = encoder
                return if (encoder != null) {
                    encoder(it.key) + "=" + encoder(it.value ?: "")
                } else {
                    urlEncode(it.key) + "=" + urlEncode(it.value ?: "")
                }
            }.joined(separator = "&")
        }
        return null
    }

    /**
     * クエリ文字列からKey-Valueのリストを取得する
     */
    fun getParameter(query: String?): List<KeyValue> {
        val query = query ?: return listOf()
        return query.components(separatedBy = "&").map {
            val pairs = it.components(separatedBy = "=")
            val key = pairs.safeGet(0)?.removingPercentEncoding ?: ""
            val value = if (1 < pairs.size) pairs[1].removingPercentEncoding else null
            KeyValue(key = key, value = value)
        }
    }


    fun urlEncode(str: String): String {
        return URLEncoder.encode(str, "UTF-8")
    }
}
