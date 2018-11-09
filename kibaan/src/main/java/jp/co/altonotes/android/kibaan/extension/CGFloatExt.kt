package jp.co.altonotes.android.kibaan.extension

import jp.co.altonotes.android.kibaan.ios.CGFloat
import jp.co.altonotes.android.kibaan.ios.NSDecimalNumber
import jp.co.altonotes.android.kibaan.ios.description

val CGFloat.stringValue: String
    get() = description

fun CGFloat.stringValue(decimalLength: Int): String {
    if (isNaN()) {
        return description
    }
    return NSDecimalNumber(value = this).stringValue(decimalLength = decimalLength)
}