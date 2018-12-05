package kibaan.extension

import kibaan.ios.CGFloat
import kibaan.ios.NSDecimalNumber
import kibaan.ios.description

val CGFloat.stringValue: String
    get() = description

fun CGFloat.stringValue(decimalLength: Int): String {
    if (isNaN()) {
        return description
    }
    return NSDecimalNumber(value = this).stringValue(decimalLength = decimalLength)
}