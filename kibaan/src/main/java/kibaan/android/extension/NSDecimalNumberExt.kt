package kibaan.android.extension

import kibaan.android.ios.NSDecimalNumber
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 指定した小数桁まで切り下げる
 */
fun NSDecimalNumber.roundDown(scale: Int = 0): NSDecimalNumber {
    val bigDecimal = bigDecimal ?: return this
    return NSDecimalNumber(bigDecimal.setScale(scale, BigDecimal.ROUND_FLOOR))
}

/**
 * 指定した小数桁まで切り上げる
 */
fun NSDecimalNumber.roundUp(scale: Int = 0): NSDecimalNumber {
    val bigDecimal = bigDecimal ?: return this
    return NSDecimalNumber(bigDecimal.setScale(scale, BigDecimal.ROUND_CEILING))
}

/**
 * 指定した小数桁まで切り捨てる
 * 絶対値が小さくなる方向に切り捨て（scale0の場合、-0.1 → 0.0）
 */
fun NSDecimalNumber.roundAbsDown(scale: Int = 0): NSDecimalNumber {
    val bigDecimal = bigDecimal ?: return this
    return NSDecimalNumber(bigDecimal.setScale(scale, BigDecimal.ROUND_DOWN))
}

/**
 * 指定した小数桁まで切り上げる
 * 絶対値が大きくなる方向に切り上げ（scale0の場合、-0.1 → -1.0）
 */
fun NSDecimalNumber.roundAbsUp(scale: Int = 0): NSDecimalNumber {
    val bigDecimal = bigDecimal ?: return this
    return NSDecimalNumber(bigDecimal.setScale(scale, BigDecimal.ROUND_UP))
}

/**
 * 指定した小数桁でまで四捨五入する
 */
fun NSDecimalNumber.roundPlain(scale: Int = 0): NSDecimalNumber {
    val bigDecimal = bigDecimal ?: return this
    return NSDecimalNumber(bigDecimal.setScale(scale, RoundingMode.HALF_UP))
}

/**
 * 指定した小数桁で四捨五入した文字列を返す
 */
fun NSDecimalNumber.stringValue(decimalLength: Int): String {
    return String.format("%.${(decimalLength)}f", args = *arrayOf(roundPlain(decimalLength).doubleValue))
}