package kibaan.android.ios

import java.util.*

/**
 * Compare two `Date` values.
 */
fun Date.compare(other: Date): ComparisonResult {
    return when {
        1 <= this.compareTo(other) -> ComparisonResult.orderedDescending
        this.compareTo(other) == 0 -> ComparisonResult.orderedSame
        else -> ComparisonResult.orderedAscending
    }
}
