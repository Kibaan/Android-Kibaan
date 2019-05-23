package kibaan.android.ios

typealias CGFloat = Double

/**
 * A textual representation of `self`.
 */
val CGFloat.description: String get() = String.format("%f", this)

/**
 * The least positive normal number.
 */
val Double.Companion.leastNormalMagnitude: CGFloat get() = MIN_VALUE

/**
 * A quiet NaN ("not a number").
 */
val Double.Companion.nan: CGFloat get() = Double.NaN

/**
 * A Boolean value indicating whether the instance is NaN ("not a number").
 */
val Double.isNaN: Boolean get() = isNaN()

operator fun Double.Companion.invoke(value: Int): Double {
    return value.toDouble()
}

operator fun Double.Companion.invoke(value: Long): Double {
    return value.toDouble()
}

operator fun Double.Companion.invoke(value: Float): Double {
    return value.toDouble()
}

operator fun Double.Companion.invoke(value: Double): Double {
    return value
}