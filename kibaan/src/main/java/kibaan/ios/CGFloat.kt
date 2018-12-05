package kibaan.ios

typealias CGFloat = Double

val CGFloat.description: String get() = String.format("%f", this)

val Double.Companion.leastNormalMagnitude: CGFloat get() = MIN_VALUE

val Double.Companion.nan: CGFloat get() = Double.NaN

val Double.isNaN: Boolean get() = isNaN()