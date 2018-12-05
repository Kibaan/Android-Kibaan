package kibaan.android.extension

import kibaan.android.ios.CGFloat
import kibaan.android.ios.NSDecimalNumber
import kibaan.android.ios.description

val CGFloat.stringValue: String
    get() = description

fun CGFloat.stringValue(decimalLength: Int): String {
    if (isNaN()) {
        return description
    }
    return NSDecimalNumber(value = this).stringValue(decimalLength = decimalLength)
}