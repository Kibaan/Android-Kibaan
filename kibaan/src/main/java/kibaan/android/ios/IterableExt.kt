package kibaan.android.ios

inline fun <T, R> Iterable<T>.reduce(initial: R, operation: (acc: R, T) -> R): R {
    return fold(initial, operation)
}

val <T> Iterable<T>.first: T?
    get() = firstOrNull()

val <T> Iterable<T>.last: T?
    get() = lastOrNull()

val <T> Iterable<T>.isEmpty: Boolean
    get() = count() == 0

fun <T> Iterable<T>.min(by: (T, T) -> Boolean): T? {
    return minWith(Comparator { o1, o2 ->
        if (by(o1, o2)) -1 else 1
    })
}

fun <T> Iterable<T>.max(by: (T, T) -> Boolean): T? {
    return maxWith(Comparator { o1, o2 ->
        if (by(o1, o2)) -1 else 1
    })
}

fun <T> Iterable<T>.dropFirst(k: Int = 1): List<T> {
    return drop(k)
}