package jp.co.altonotes.android.kibaan.ios

/**
 *
 * Created by Yamamoto Keita on 2018/01/18.
 */

fun <T> MutableList<T>.removeFirst(): T {
    return removeAt(0)
}

fun <T> MutableList<T>.removeLast(): T? {
    if (size == 0) return null
    val element: T = get(size - 1)
    removeAt(size - 1)
    return element
}

fun <T> MutableList<T>.removeSubrange(range: IntRange) {
    if (range.first < range.last) {
        val remove = subList(range.first, range.last).toList()
        removeAll(remove)
    }
}

fun <T> List<T>.safeGet(index: Int): T? {
    if (index in 0..(size - 1)) {
        return this[index]
    }
    return null
}

fun <T> MutableList<T>.insert(value: T, index: Int) {
    add(index, value)
}

fun <T> List<T>.prefix(maxLength: Int): List<T> {
    return take(maxLength)
}

fun <T> List<T>.suffix(maxLength: Int): List<T> {
    return takeLast(maxLength)
}