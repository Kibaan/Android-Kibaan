package kibaan.android.extension

import android.content.Context
import android.util.TypedValue

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.dpToPx(dp: Double): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.spToPx(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics).toInt()
}

fun Context.spToPx(sp: Double): Int {
    return spToPx(sp.toFloat())
}

fun Context.pxToDp(px: Int): Float {
    return pxToDp(px.toFloat())
}

fun Context.pxToDp(px: Float): Float {
    return (px / resources.displayMetrics.density)
}

fun Context.pxToSp(px: Int): Float {
    return pxToSp(px.toFloat())
}

fun Context.pxToSp(px: Float): Float {
    return px / resources.displayMetrics.scaledDensity
}