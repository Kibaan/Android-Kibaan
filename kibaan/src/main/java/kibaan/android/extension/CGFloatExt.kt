package kibaan.android.extension

import kibaan.android.ios.CGFloat
import kibaan.android.ios.NSDecimalNumber
import kibaan.android.ios.description

/**
 * 数値を文字列に変換して返す
 */
val CGFloat.stringValue: String
    get() = description

val CGFloat.doubleValue: Double
    get() = this

val CGFloat.decimalNumber: NSDecimalNumber
    get() = NSDecimalNumber(value = this)

/**
 * 数値を指定した小数桁で四捨五入した文字列を返す
 */
fun CGFloat.stringValue(decimalLength: Int): String {
    if (isNaN()) {
        return description
    }
    return NSDecimalNumber(value = this).stringValue(decimalLength = decimalLength)
}