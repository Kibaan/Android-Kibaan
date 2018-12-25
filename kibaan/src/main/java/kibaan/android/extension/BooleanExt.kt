package kibaan.android.extension

import java.util.*

val Boolean?.isTrue: Boolean
    get() {
        return this == true
    }

fun Boolean.toggled(): Boolean {
    return !this
}

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
