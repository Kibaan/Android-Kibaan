package kibaan.android.valueobject

import kibaan.android.ios.append

/**
 * 順序を保持するDictionary
 */
class ListDictionary<Key, Value>(elements: Map<Key, Value>) {

    private var keyList: MutableList<Key> = mutableListOf()
    private var dictionary: MutableMap<Key, Value> = mutableMapOf()
    val keys: List<Key>
        get() = keyList

    init {
        for (entry in elements) {
            keyList.append(entry.key)
            dictionary[entry.key] = entry.value
        }
    }

    operator fun get(key: Key): Value? {
        return dictionary[key]
    }

    operator fun get(index: Int): Value? {
        return dictionary[keyList[index]]
    }
}