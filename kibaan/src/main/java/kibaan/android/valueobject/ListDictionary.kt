package kibaan.android.valueobject

import kibaan.android.ios.append
import kibaan.android.ios.removeAll

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

    operator fun set(key: Key, value: Value?) {
        if (value == null) {
            remove(key = key)
            return
        }
        append(key, value)
    }

    operator fun get(index: Int): Value? {
        if (index < 0 || keyList.size <= index) {
            return null
        }
        return dictionary[keyList[index]]
    }

    operator fun set(index: Int, value: Value) {
        if (value == null) {
            remove(index = index)
            return
        }
        append(key = keyList[index], value = value)
    }

    fun append(key: Key, value: Value) {
        if (!dictionary.containsKey(key)) {
            keyList.append(key)
        }
        dictionary[key] = value
    }

    fun remove(key: Key) {
        keyList.remove(key)
        dictionary.remove(key)
    }

    fun remove(index: Int) {
        if (index < 0 || keyList.size <= index) {
            return
        }

        val key = keyList[index]
        keyList.removeAt(index)
        dictionary.remove(key)
    }

    fun removeAll() {
        keyList.removeAll()
        dictionary.removeAll()
    }
}