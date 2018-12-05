package kibaan.util

fun <T> min(a: T, b: T): T where T : Comparable<T> {
    return if (b < a) {
        b
    } else {
        a
    }
}

fun <T> max(a: T, b: T): T where T : Comparable<T> {
    return if (a < b) {
        b
    } else {
        a
    }
}