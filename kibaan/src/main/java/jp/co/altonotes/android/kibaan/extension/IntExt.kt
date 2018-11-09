package jp.co.altonotes.android.kibaan.extension

import jp.co.altonotes.android.kibaan.controller.SmartActivity
import java.util.*

val Int?.stringValue: String
    get() {
        if (this == null) {
            return ""
        }
        return this.toString()
    }

val Long?.stringValue: String
    get() {
        if (this == null) {
            return ""
        }
        return this.toString()
    }

/**
 * ランダムな数値を取得する
 */
fun Int.Companion.random(min: Int, max: Int): Int {
    val range = max - min + 1
    val random = Random().nextInt(range)
    return min + random
}

/**
 * ローカライズされた文字列を取得する
 */
val Int.localizedString: String
    get() {
        val context = SmartActivity.sharedOrNull ?: return "Context is null"
        return context.resources.getString(this)
    }