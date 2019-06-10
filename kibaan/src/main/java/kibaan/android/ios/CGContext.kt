package kibaan.android.ios

import android.content.Context
import android.graphics.*
import kibaan.android.extension.dpToPx
import kotlin.math.PI

private var currentContext: CGContext? = null

fun UIGraphicsGetCurrentContext(context: Context, canvas: Canvas): CGContext {
    val context = CGContext(canvas, context)
    currentContext = context
    return context
}

/**
 * 描画コンテキスト
 * Created by Yamamoto Keita on 2018/05/26.
 */
class CGContext(val canvas: Canvas, val context: Context) {

    private var param = Param()

    private var savedParam: Param? = null

    private var path = Path()

    //region Set parameters ---------------

    fun setFillColor(color: UIColor) {
        param.fillColor = color
    }

    fun setStrokeColor(color: UIColor) {
        param.strokeColor = color
    }

    fun setShouldAntialias(shouldAntialias: Boolean) {
        param.shouldAntialias = shouldAntialias
    }

    fun setLineWidth(width: CGFloat) {
        param.lineWidth = width
    }

    fun setLineDash(phase: Int, lengths: List<CGFloat>) {
        param.lineDashPhase = phase
        param.lineDashLengths = lengths
    }

    fun setLineJoin(lineJoin: CGLineJoin) {
        param.lineJoin = lineJoin
    }

    fun clip(to: CGRect) {
        canvas.clipRect(to.rectF)
    }

    fun clip(using: CGPathFillRule = CGPathFillRule.winding) {
        canvas.clipPath(path)
    }

    //endregion

    fun saveGState() {
        canvas.save()
        savedParam = param.clone()
    }

    fun restoreGState() {
        canvas.restore()
        val savedParam = savedParam
        if (savedParam != null) {
            param = savedParam
        }
    }

    //region Path making ---------------
    fun beginPath() {
        clearPath()
    }

    fun move(to: CGPoint) {
        path.moveTo(to.x.toFloat(), to.y.toFloat())
    }

    fun addLine(to: CGPoint) {
        path.lineTo(to.x.toFloat(), to.y.toFloat())
    }

    fun addLines(between: List<CGPoint>) {
        between.forEachIndexed { index, point ->
            if (index == 0) {
                path.moveTo(point.x.toFloat(), point.y.toFloat())
            } else {
                path.lineTo(point.x.toFloat(), point.y.toFloat())
            }
        }
    }

    fun addRect(rect: CGRect) {
        path.addRect(rect.rectF, Path.Direction.CW)
    }

    fun addEllipse(rect: CGRect) {
        path.addOval(rect.rectF, Path.Direction.CW)
    }

    fun addArc(center: CGPoint, radius: CGFloat, startAngle: CGFloat, endAngle: CGFloat, clockwise: Boolean) {
        val oval = RectF((center.x - radius).toFloat(), (center.y - radius).toFloat(), (center.x + radius).toFloat(), (center.y + radius).toFloat())
        val startDegree = startAngle * 180 / PI
        val endDegree = endAngle * 180 / PI
        val sweepDegree = if (clockwise) {
            startDegree - endDegree
        } else {
            endDegree - startDegree
        }
        path.addArc(oval, startDegree.toFloat(), sweepDegree.toFloat())
    }

    fun closePath() {
        path.close()
    }

    // endregion
    fun fill(rect: CGRect) {
        param.fillMode()
        canvas.drawRect(rect.rectF, param.paint)
    }

    fun fillEllipse(rect: CGRect) {
        param.fillMode()
        canvas.drawOval(rect.rectF, param.paint)
    }

    fun stroke(rect: CGRect) {
        param.strokeMode()
        canvas.drawRect(rect.rectF, param.paint)
    }

    fun strokePath() {
        param.strokeMode()
        canvas.drawPath(path, param.paint)
        clearPath()
    }

    fun strokeEllipse(rect: CGRect) {
        addEllipse(rect)
        strokePath()
    }

    fun fillPath() {
        param.fillMode()
        canvas.drawPath(path, param.paint)
        clearPath()
    }

    // テキストを描画する
    // 引数のpointは左上
    fun drawText(text: String, point: CGPoint, font: UIFont, color: UIColor) {
        param.paint.typeface = font.typeface
        param.paint.textSize = context.dpToPx(font.pointSize).toFloat()
        param.fillColor = color
        param.fillMode()

        val oldShouldAntialias = param.shouldAntialias
        param.shouldAntialias = true

        // canvas.drawTextの引数は左下なので、フォント高分下に下げる必要がある
        val height = "9".size(context, font).height
        canvas.drawText(text, point.x.toFloat(), point.y.toFloat() + height.toFloat(), param.paint)

        param.shouldAntialias = oldShouldAntialias
    }

    // テキストを描画する。文字列の左上とrectの左上を合わせる
    fun drawTextInRect(text: String, rect: CGRect, font: UIFont, color: UIColor) {
        param.paint.typeface = font.typeface
        param.paint.textSize = context.dpToPx(font.pointSize).toFloat()
        param.fillColor = color
        param.fillMode()

        val oldShouldAntialias = param.shouldAntialias
        param.shouldAntialias = true

        val height = "9".size(context, font).height
        canvas.drawText(text, rect.origin.x.toFloat(), rect.origin.y.toFloat() + height.toFloat(), param.paint)

        param.shouldAntialias = oldShouldAntialias
    }

    fun drawRadialGradient(colors: List<UIColor>, center: CGPoint, radius: CGFloat) {
        val gradient = RadialGradient(center.x.toFloat(), center.y.toFloat(), radius.toFloat(),
                colors.first().intValue, colors.last().intValue, android.graphics.Shader.TileMode.CLAMP)

        param.paint.style = Paint.Style.FILL
        param.paint.isDither = true // 端を滑らかにする
        param.paint.shader = gradient

        canvas.drawCircle(center.x.toFloat(), center.y.toFloat(), radius.toFloat(), param.paint)
    }

    fun drawImage(image: Bitmap, rect: CGRect) {
        val srcRect = Rect(0, 0, image.width, image.height)
        canvas.drawBitmap(image, srcRect, rect.rectF, param.paint)
    }

    /**
     * This does nothing. This method is for iOS compatibility
     */
    fun flush() {
    }

    private fun clearPath() {
        path = Path()
    }

    /**
     * 描画パラメーター
     */
    class Param {

        val paint = Paint()

        var fillColor: UIColor? = null
        var strokeColor: UIColor? = null
        var lineDashPhase: Int? = null
        var lineDashLengths: List<CGFloat>? = null

        var shouldAntialias: Boolean = false
            set(value) {
                field = value
                paint.isAntiAlias = shouldAntialias
                paint.isFilterBitmap = shouldAntialias
            }

        var lineWidth: CGFloat = 1.0
            set(value) {
                field = value
                paint.strokeWidth = lineWidth.toFloat()
            }

        var lineJoin: CGLineJoin? = null

        constructor()

        constructor(fillColor: UIColor?,
                    strokeColor: UIColor?,
                    lineDashPhase: Int?,
                    lineDashLengths: List<CGFloat>?,
                    shouldAntialias: Boolean,
                    lineJoin: CGLineJoin?) {
            this.fillColor = fillColor
            this.strokeColor = strokeColor
            this.lineDashPhase = lineDashPhase
            this.lineDashLengths = lineDashLengths
            this.shouldAntialias = shouldAntialias
            this.lineJoin = lineJoin
        }

        fun fillMode() {
            paint.style = Paint.Style.FILL
            paint.pathEffect = null
            val color = fillColor?.intValue
            if (color != null) {
                paint.color = color
            }
        }

        fun strokeMode() {
            paint.style = Paint.Style.STROKE

            val dashLengths = lineDashLengths
            if (dashLengths != null && dashLengths.isNotEmpty()) {
                val phase = lineDashPhase?.toFloat() ?: 0.0f
                val floatArray = dashLengths.map { it.toFloat() }.toFloatArray()
                paint.pathEffect = DashPathEffect(floatArray, phase)
            } else {
                paint.pathEffect = null
            }

            val color = strokeColor?.intValue
            if (color != null) {
                paint.color = color
            }

            paint.strokeJoin = getStokeJoin(lineJoin)
        }

        fun clone(): Param {
            return Param(fillColor,
                strokeColor,
                lineDashPhase,
                lineDashLengths,
                shouldAntialias,
                lineJoin)
        }

        private fun getStokeJoin(join: CGLineJoin?): Paint.Join {
            return when (join) {
                CGLineJoin.miter -> Paint.Join.MITER
                CGLineJoin.round -> Paint.Join.ROUND
                CGLineJoin.bevel -> Paint.Join.BEVEL
                else -> Paint.Join.MITER
            }
        }
    }
}

// 文字列の左上を基点座標にして描画する
fun String.draw(at: CGPoint, withAttributes: Map<NSAttributedString.Key, Any>?) {
    TODO("not implemented")

    val font: UIFont = withAttributes?.get(NSAttributedString.Key.font) as? UIFont ?: UIFont.systemFont(14.0)
    val color: UIColor = withAttributes?.get(NSAttributedString.Key.foregroundColor) as? UIColor ?: UIColor.black
    val paragraphStyle: NSParagraphStyle = withAttributes?.get(NSAttributedString.Key.paragraphStyle) as? NSParagraphStyle ?: NSMutableParagraphStyle()

}

// 文字列の左上をRectの左上と合わせて描画する（attributesのParagraphStyleで、alignmentが指定された場合は中央寄せ、右寄せも可能）
// Rectに文字が収まりきらない場合、標準では折り返すがattributesのParagraphStyleの指定により省略表示も可能
fun String.draw(inRect: CGRect, withAttributes: Map<NSAttributedString.Key, Any>? = null) {
    TODO("not implemented")
}

// enumは用意したが今のところ使われない
enum class CGPathFillRule {
    winding,
    // evenOdd is not supported
    ;
}

enum class CGLineJoin : IntEnumDefault {
    miter,
    round,
    bevel,
}