package kibaan.android.ios


enum class ComparisonResult {
    orderedAscending, orderedSame, orderedDescending
}

fun Int.toComparisonResult(): ComparisonResult {
    return if (0 < this) {
        ComparisonResult.orderedDescending
    } else if (this < 0) {
        ComparisonResult.orderedAscending
    } else {
        ComparisonResult.orderedSame
    }
}