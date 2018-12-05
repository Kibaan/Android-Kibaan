package kibaan.android.ios

import android.graphics.Typeface
import kibaan.android.extension.isTrue

class UIFont(val typeface: Typeface?, val pointSize: CGFloat) {

    val isBold: Boolean get() = typeface?.isBold.isTrue

    constructor(typeface: Typeface?, pointSize: Float) : this(typeface, pointSize.toDouble())

    fun withSize(size: Double): UIFont {
        return UIFont(typeface = typeface, pointSize = size)
    }

    companion object {
        fun boldSystemFont(ofSize: CGFloat): UIFont {
            return UIFont(Typeface.DEFAULT_BOLD, ofSize)
        }

        fun systemFont(ofSize: CGFloat): UIFont {
            return UIFont(Typeface.DEFAULT, ofSize)
        }
    }
}