package kibaan.android.extension

import java.util.*

/**
 * Bool値がTrueかどうかを返す
 */
val Boolean?.isTrue: Boolean
    get() {
        return this == true
    }

/**
 * Bool値を逆転して返す
 */
fun Boolean.toggled(): Boolean {
    return !this
}

/**
 * Bool値を文字列に変換して返す
 */
val Boolean.stringValue: String
    get() {
        return toString()
    }

object Bool {
    /**
     * ランダムなBool値を取得する
     */
    fun random(): Boolean {
        return Random().nextInt(2) == 0
    }
}
