package kibaan.android.ios

class Dictionary<K, V> : LinkedHashMap<K, V>() {

    operator fun set(key: K, value: V?) {
        if (value != null) {
            put(key, value)
        } else {
            remove(key)
        }
    }
}

fun <K, V> dictionaryOf(vararg pairs: Pair<K, V>): Dictionary<K, V> = Dictionary<K, V>().apply { putAll(pairs) }