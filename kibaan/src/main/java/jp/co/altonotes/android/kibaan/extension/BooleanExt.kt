package jp.co.altonotes.android.kibaan.extension

import java.util.*

val Boolean?.isTrue: Boolean
    get() {
        return this == true
    }

object Bool {
    /**
     * ランダムなBool値を取得する
     */
    fun random(): Boolean {
        return Random().nextInt(2) == 0
    }
}
