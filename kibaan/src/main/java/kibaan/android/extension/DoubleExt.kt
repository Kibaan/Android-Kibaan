package kibaan.android.extension

import kibaan.android.ios.CGFloat
import kotlin.math.floor

/**
 * CGFloatに変換して返す
 */
val Double.cgFloatValue: CGFloat
    get() = this

/**
 * 余りを返す
 */
fun Double.truncatingRemainder(dividingBy: Double) : Double {
    val q: Double = floor((this / dividingBy))
    return this - dividingBy * q
}