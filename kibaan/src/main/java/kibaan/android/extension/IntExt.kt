package kibaan.android.extension

import kibaan.android.framework.SmartActivity
import kibaan.android.ios.CGFloat
import java.util.*

/**
 * 数値を文字列に変換して返す
 */
val Int?.stringValue: String
    get() {
        if (this == null) {
            return ""
        }
        return this.toString()
    }

/**
 * 数値を文字列に変換して返す
 */
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

/**
 * CGFloatに変換して返す
 */
val Int.cgFloatValue: CGFloat
    get() = this.toDouble()