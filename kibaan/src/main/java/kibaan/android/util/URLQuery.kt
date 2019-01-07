package kibaan.android.util

import kibaan.android.extension.urlEncoded
import kibaan.android.ios.*
import kibaan.android.valueobject.KeyValue

/**
 * key=valueの&つなぎ形式のクエリ
 */
open class URLQuery {

    open val stringValue: String
        get() = keyValues.map {
            var item = it.key.urlEncoded
            val value = it.value?.urlEncoded
            if (value != null) {
                item += "=${value}"
            }
            item
        }.joined(separator = "&")

    var keyValues: MutableList<KeyValue> = mutableListOf()

    constructor()

    constructor(string: String?) {
        val string = string?.trim() ?: return
        keyValues = string
            .components(separatedBy = "&")
            .filter { it.isNotEmpty() }
            .map {
                val pairs = it.components(separatedBy = "=")
                val key = pairs[0].removingPercentEncoding
                val value = if (1 < pairs.size) pairs[1].removingPercentEncoding else null
                KeyValue(key = key, value = value)
            }
            .toMutableList()
    }

    constructor(vararg elements: Pair<String, String?>) {
        this.keyValues = elements.map { KeyValue(it.first, it.second) }.toMutableList()
    }

    constructor(keyValues: List<KeyValue>) {
        this.keyValues = keyValues.toMutableList()
    }

    operator fun get(key: String): String? {
        return keyValues.firstOrNull { it.key == key }?.value
    }

    operator fun set(key: String, value: String?) {
        val keyValue = KeyValue(key = key, value = value)
        val offset = keyValues.indexOrNull { it.key == key }
        if (offset != null) {
            keyValues[offset] = keyValue
        } else {
            keyValues.append(keyValue)
        }
    }


    fun stringValueCustomEncoded(encoder: (String) -> String): String {
        return keyValues.map {
            var item = encoder(it.key)
            val value = it.value
            if (value != null) {
                item += "=${encoder(value)}"
            }
            item
        }.joined(separator = "&")
    }

}
