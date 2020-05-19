package kibaan.android.ios

import android.graphics.RectF
import kibaan.android.extension.height
import kibaan.android.extension.width

data class CGRect(var origin: CGPoint = CGPoint.zero, var size: CGSize = CGSize.zero) {

    companion object {
        /**
         * 原点およびサイズがゼロの矩形
         */
        val zero = CGRect(0.0, 0.0, 0.0, 0.0)
    }

    /**
     * 矩形の左端のX軸位置
     */
    val minX: CGFloat
        get() = origin.x

    /**
     * 矩形の上端のY軸位置
     */
    val minY: CGFloat
        get() = origin.y

    /**
     * 矩形の右端のX軸位置
     */
    val maxX: CGFloat
        get() = origin.x + size.width

    /**
     * 矩形の下端のX軸位置
     */
    val maxY: CGFloat
        get() = origin.y + size.height

    /**
     * 矩形の中心のX軸位置
     */
    val midX: CGFloat
        get() = origin.x + (size.width / 2)

    /**
     * 矩形の中心のY軸位置
     */
    val midY: CGFloat
        get() = origin.y + (size.height / 2)

    /**
     * RectF型に変換
     */
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

    /**
     * CGFloatで原点とサイズを指定して矩形を作成する
     */
    constructor(x: CGFloat, y: CGFloat, width: CGFloat, height: CGFloat) : this(
        origin = CGPoint(x, y),
        size = CGSize(width, height)
    )

    /**
     * Intで原点とサイズを指定して矩形を作成する
     */
    constructor(x: Int, y: Int, width: Int, height: Int) : this(
        origin = CGPoint(x.toDouble(), y.toDouble()),
        size = CGSize(width.toDouble(), height.toDouble())
    )

    /**
     * コピーした矩形を返す
     */
    fun copy(): CGRect {
        return CGRect(origin.x, origin.y, width, height)
    }

    /**
     * 指定されたポイントが矩形に含まれているかどうかを返す
     */
    fun contains(point: CGPoint): Boolean {
        val x = point.x
        val y = point.y
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY
    }
}