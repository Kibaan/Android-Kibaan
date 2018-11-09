package jp.co.altonotes.android.kibaan.extension

/**
 * 要素を追加する
 */
operator fun <T> MutableCollection<T>.plusAssign(element: T) {
    add(element)
}