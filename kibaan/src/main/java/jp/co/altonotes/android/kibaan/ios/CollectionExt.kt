package jp.co.altonotes.android.kibaan.ios

fun <T> MutableCollection<T>.append(newElement: T) {
    add(newElement)
}

fun <T> MutableCollection<T>.append(contentsOf: Collection<T>) {
    addAll(contentsOf)
}

fun <T> MutableCollection<T>.removeAll() {
    clear()
}

fun <T> MutableCollection<T>.remove(elements: Collection<T>) {
    removeAll(elements = elements)
}

val <T> Collection<T>.count: Int get() = size

fun Collection<String>.joined(separator: String = ""): String {
    return joinToString(separator = separator)
}

fun <T> Collection<T>.index(of: T): Int {
    return indexOf(of)
}

fun <T> Collection<T>.indexOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst(predicate)
    if (index == -1) {
        return null
    }
    return index
}

fun <T> Collection<T>.contains(where: (T) -> Boolean): Boolean {
    return firstOrNull(where) != null
}

fun <T, R : Any> Collection<T>.flatMap(transform: (T) -> R?): List<R> {
    return mapNotNull(transform)
}

fun <T> Collection<T>.enumerated(): List<CollectionEnumerationItem<T>> {
    return mapIndexed { index, element ->
        CollectionEnumerationItem(index, element)
    }
}

fun <T, R : Any> Collection<T>.compactMap(transform: (T) -> R?): List<R> {
    return mapNotNull(transform)
}

data class CollectionEnumerationItem<T>(val offset: Int, val element: T)
