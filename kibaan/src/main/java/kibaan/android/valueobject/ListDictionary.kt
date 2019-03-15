package kibaan.android.valueobject

import kibaan.android.ios.append

/**
 * 順序を保持するDictionary
 */
class ListDictionary(elements: Map<String, String>) {
    var keyList: MutableList<String> = mutableListOf()
    var dictionary: MutableMap<String, String> = mutableMapOf<String, String>()
    val keys: List<String>
        get() = keyList

    init {
        for (entry in elements) {
            keyList.append(entry.key)
            dictionary[entry.key] = entry.value
        }
    }

    operator fun get(key: String): String? {
        return dictionary[key]
    }

    operator fun get(index: Int): String? {
        return dictionary[keyList[index]]
    }
}