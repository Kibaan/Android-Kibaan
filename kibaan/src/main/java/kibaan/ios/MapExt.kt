package kibaan.ios

fun <K, T> MutableMap<K, T>.removeAll() {
    clear()
}

fun <K, T> Map<K, T>.first(where: (Map.Entry<K, T>) -> Boolean): Map.Entry<K, T>? {
    entries.forEach {
        if (where(it)) {
            return it
        }
    }
    return null
}

fun <K, T> MutableMap<K, T>.removeValue(forKey: K) {
    remove(forKey)
}