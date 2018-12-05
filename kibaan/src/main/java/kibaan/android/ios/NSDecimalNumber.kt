package kibaan.android.ios

import java.math.BigDecimal

class NSDecimalNumber : Comparable<NSDecimalNumber> {

    companion object {
        val zero = NSDecimalNumber(0)
        val one = NSDecimalNumber(1)
        val notANumber = NSDecimalNumber(null)
    }

    var bigDecimal: BigDecimal? = null

    // region -> Constructor

    constructor(string: String?) {
        if (string == null) {
            return
        }

        bigDecimal = try {
            BigDecimal(string)
        } catch (e: NumberFormatException) {
            null
        }
    }

    constructor(value: Int) {
        bigDecimal = BigDecimal(value)
    }

    constructor(value: Long) {
        bigDecimal = BigDecimal(value)
    }

    constructor(value: Double) {
        bigDecimal = try {
            BigDecimal.valueOf(value)
        } catch (e: NumberFormatException) {
            null
        }
    }

    constructor(bigDecimal: BigDecimal) {
        this.bigDecimal = bigDecimal
    }

    // endregion

    // region -> Function

    fun adding(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.add(decimalNumber.bigDecimal))
    }

    fun adding(value: Int): NSDecimalNumber {
        return adding(NSDecimalNumber(value))
    }

    fun subtracting(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.subtract(decimalNumber.bigDecimal))
    }

    fun subtracting(value: Int): NSDecimalNumber {
        return subtracting(NSDecimalNumber(value))
    }

    fun multiplying(by: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.multiply(by.bigDecimal))
    }

    fun dividing(by: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.divide(by.bigDecimal))
    }

    fun dividing(by: Int): NSDecimalNumber {
        return dividing(NSDecimalNumber(by))
    }

    fun raising(toPower: Int): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.pow(toPower))
    }

    override fun equals(other: Any?): Boolean {
        val decimalNumber = (other as? NSDecimalNumber) ?: return false
        return bigDecimal == decimalNumber.bigDecimal
    }

    override fun hashCode(): Int {
        return bigDecimal?.hashCode() ?: super.hashCode()
    }

    // endregion

    // region -> Computed Property

    val stringValue: String
        get() = if (bigDecimal != null) bigDecimal.toString() else "nan"

    val doubleValue: Double
        get() = bigDecimal?.toDouble() ?: Double.NaN

    // endregion

    fun compare(decimalNumber: NSDecimalNumber): ComparisonResult {
        val lhs = bigDecimal
        val rhs = decimalNumber.bigDecimal
        if (lhs == null && rhs == null) {
            return ComparisonResult.orderedSame
        }
        if (rhs == null) {
            return ComparisonResult.orderedDescending
        }
        if (lhs == null) {
            return ComparisonResult.orderedAscending
        }
        return when {
            lhs.compareTo(rhs) >= 1 -> ComparisonResult.orderedDescending
            lhs.compareTo(rhs) == 0 -> ComparisonResult.orderedSame
            else -> ComparisonResult.orderedAscending
        }
    }

    override operator fun compareTo(decimalNumber: NSDecimalNumber): Int {
        val result = compare(decimalNumber)
        return when (result) {
            ComparisonResult.orderedAscending -> -1
            ComparisonResult.orderedSame -> 0
            ComparisonResult.orderedDescending -> 1
        }
    }

}