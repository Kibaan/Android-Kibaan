package jp.co.altonotes.android.kibaan.ios

import android.graphics.RectF
import jp.co.altonotes.android.kibaan.extension.height
import jp.co.altonotes.android.kibaan.extension.width

data class CGRect(var origin: CGPoint = CGPoint.zero, var size: CGSize = CGSize.zero) {

    companion object {
        val zero = CGRect(0.0, 0.0, 0.0, 0.0)
    }

    val minX: CGFloat
        get() = origin.x

    val minY: CGFloat
        get() = origin.y

    val maxX: CGFloat
        get() = origin.x + size.width

    val maxY: CGFloat
        get() = origin.y + size.height

    val rectF: RectF
        get() {
            val x1 = origin.x.toFloat()
            val y1 = origin.y.toFloat()
            val x2 = maxX.toFloat()
            val y2 = maxY.toFloat()

            val left = Math.min(x1, x2)
            val top = Math.min(y1, y2)
            val right = Math.max(x1, x2)
            val bottom = Math.max(y1, y2)

            return RectF(left, top, right, bottom)
        }

    constructor(x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) : this(origin = CGPoint(x, y), size = CGSize(width, height))

    constructor(x: Int, y: Int, width: Int, height: Int) : this(origin = CGPoint(x.toDouble(), y.toDouble()), size = CGSize(width.toDouble(), height.toDouble()))

    fun copy(): CGRect {
        return CGRect(origin.x, origin.y, width, height)
    }
}