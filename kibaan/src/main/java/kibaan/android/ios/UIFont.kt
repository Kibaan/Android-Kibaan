package kibaan.android.ios

import android.graphics.Typeface
import kibaan.android.extension.isTrue

class UIFont(val typeface: Typeface?, val pointSize: CGFloat, val sizeUnit: FontSizeUnit = FontSizeUnit.sp) {

    val isBold: Boolean get() = typeface?.isBold.isTrue

    constructor(typeface: Typeface?, pointSize: Float) : this(typeface, pointSize.toDouble())

    fun withSize(size: Double): UIFont {
        return UIFont(typeface = typeface, pointSize = size)
    }

    companion object {
        fun boldSystemFont(ofSize: CGFloat, sizeUnit: FontSizeUnit = FontSizeUnit.sp): UIFont {
            return UIFont(Typeface.DEFAULT_BOLD, ofSize, sizeUnit)
        }

        fun systemFont(ofSize: CGFloat, sizeUnit: FontSizeUnit = FontSizeUnit.sp): UIFont {
            return UIFont(Typeface.DEFAULT, ofSize, sizeUnit)
        }
    }
}

enum class FontSizeUnit {
    sp, dp
}