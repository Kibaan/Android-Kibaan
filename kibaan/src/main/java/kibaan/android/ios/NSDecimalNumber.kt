package kibaan.android.ios

import java.math.BigDecimal

class NSDecimalNumber : Comparable<NSDecimalNumber> {

    companion object {
        /**
         * 値が"0"のインスタンス
         */
        val zero = NSDecimalNumber(0)
        /**
         * 値が"1"のインスタンス
         */
        val one = NSDecimalNumber(1)
        /**
         * 値が"null"のインスタンス
         */
        val notANumber = NSDecimalNumber(null)
    }

    var bigDecimal: BigDecimal? = null

    // region -> Constructor

    /**
     * 文字列を指定してインスタンスを作成する
     */
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

    /**
     * Int型の数値を指定してインスタンスを作成する
     */
    constructor(value: Int) {
        bigDecimal = BigDecimal(value)
    }

    /**
     * Long型の数値を指定してインタンスを作成する
     */
    constructor(value: Long) {
        bigDecimal = BigDecimal(value)
    }

    /**
     * Double型の数値を指定してインタスタンスを作成する
     */
    constructor(value: Double) {
        bigDecimal = try {
            BigDecimal.valueOf(value)
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * BigDecimal型の数値を指定してインスタンスを作成する
     */
    constructor(bigDecimal: BigDecimal) {
        this.bigDecimal = bigDecimal
    }

    // endregion

    // region -> Function

    /**
     * NSDecimalNumber型で指定された数値を足した結果を返す
     */
    fun adding(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.add(decimalNumber.bigDecimal))
    }

    /**
     * Int型で指定された数値を足した結果を返す
     */
    fun adding(value: Int): NSDecimalNumber {
        return adding(NSDecimalNumber(value))
    }

    /**
     * NSDecimalNumber型で指定された数値を引いた結果を返す
     */
    fun subtracting(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.subtract(decimalNumber.bigDecimal))
    }

    /**
     * Int型で指定された数値を引いた結果を返す
     */
    fun subtracting(value: Int): NSDecimalNumber {
        return subtracting(NSDecimalNumber(value))
    }

    /**
     * NSDecimalNumber型で指定された数値を掛けた結果を返す
     */
    fun multiplying(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.multiply(decimalNumber.bigDecimal))
    }

    /**
     * Int型で指定された数値を掛けた結果を返す
     */
    fun multiplying(value: Int): NSDecimalNumber {
        return multiplying(NSDecimalNumber(value))
    }

    /**
     * NSDecimalNumber型で指定された数値で割った結果を返す
     */
    fun dividing(decimalNumber: NSDecimalNumber): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.divide(decimalNumber.bigDecimal))
    }

    /**
     * Int型で指定された数値で割った結果を返す
     */
    fun dividing(value: Int): NSDecimalNumber {
        return dividing(NSDecimalNumber(value))
    }

    /**
     * Int型で指定された数値で乗算した結果を返す
     */
    fun raising(toPower: Int): NSDecimalNumber {
        val bigDecimal = bigDecimal ?: return this
        return NSDecimalNumber(bigDecimal.pow(toPower))
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    override fun equals(other: Any?): Boolean {
        val decimalNumber = (other as? NSDecimalNumber) ?: return false
        return bigDecimal == decimalNumber.bigDecimal
    }

    /**
     * Returns a hash code value for the object.
     */
    override fun hashCode(): Int {
        return bigDecimal?.hashCode() ?: super.hashCode()
    }

    // endregion

    // region -> Computed Property

    /**
     * 数値を文字列に変換して返す
     */
    val stringValue: String
        get() = if (bigDecimal != null) bigDecimal.toString() else "nan"

    /**
     * Double型の数値に変換して返す
     */
    val doubleValue: Double
        get() = bigDecimal?.toDouble() ?: Double.NaN

    // endregion

    /**
     * Compare two `NSDecimalNumber` values.
     */
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

    /**
     * Compare two `NSDecimalNumber` values.
     */
    override operator fun compareTo(decimalNumber: NSDecimalNumber): Int {
        val result = compare(decimalNumber)
        return when (result) {
            ComparisonResult.orderedAscending -> -1
            ComparisonResult.orderedSame -> 0
            ComparisonResult.orderedDescending -> 1
        }
    }

}