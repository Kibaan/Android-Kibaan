package kibaan.android.extension

/**
 * 要素を追加する
 */
operator fun <T> MutableCollection<T>.plusAssign(element: T) {
    add(element)
}