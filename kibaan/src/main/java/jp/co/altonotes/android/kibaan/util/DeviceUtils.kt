package jp.co.altonotes.android.kibaan.util

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.view.Window
import android.view.WindowManager

object DeviceUtils {

    /**
     * 端末の短辺に対する割合で長さを取得する
     */
    fun vminLength(context: Context, rate: Double): Double {
        return shortLengthPX(context) * rate
    }

    /**
     * 端末の長辺に対する割合で長さを取得する
     */
    fun vhLength(context: Context, rate: Double): Double {
        return longLengthPX(context) * rate
    }

    fun toPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun toPx(context: Context, dp: Double): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    fun toDp(context: Context, px: Int): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }

    /**
     * 端末の短辺をPXで返す
     */
    fun shortLengthPX(context: Context): Int {
        val displaySize = getDisplaySize(context)
        return Math.min(displaySize.x, displaySize.y)
    }

    /**
     * 端末の短辺をPXで返す
     */
    fun longLengthPX(context: Context): Int {
        val displaySize = getDisplaySize(context)
        return Math.max(displaySize.x, displaySize.y)
    }

    /**
     * 端末の短辺をDPで返す
     */
    fun shortLengthDP(context: Context): Float {
        return shortLengthPX(context) / context.resources.displayMetrics.density
    }

    /**
     * 端末の長辺をDPで返す
     */
    fun longLengthDP(context: Context): Float {
        return longLengthPX(context) / context.resources.displayMetrics.density
    }

    /**
     * ディスプレイのサイズ(PX)を返す
     */
    private fun getDisplaySize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        return if (17 <= Build.VERSION.SDK_INT) {
            val size = Point()
            display.getSize(size)
            size
        } else {
            @Suppress("DEPRECATION")
            Point(display.width, display.height)
        }
    }

    /**
     * ソフトウェアボタン部分の高さを返す
     */
    fun softwareKeyButtonsArea(window: Window): Point {
        val windowSize = Rect()
        window.decorView.getWindowVisibleDisplayFrame(windowSize)
        val displaySize = getDisplaySize(context = window.context)
        val width = displaySize.x - Math.abs(windowSize.width())
        val height = displaySize.y - Math.abs(windowSize.height())
        return Point(width, height)
    }
}