package kibaan.ios

import android.graphics.*




/**
 * 描画コンテキスト
 * Created by Yamamoto Keita on 2018/05/26.
 */
class CGContext(val canvas: Canvas) {

    private var param = Param()

    private var savedParam: Param? = null

    private var path = Path()

    //region Set parameters ---------------

    fun setFill(color: UIColor) {
        param.fillColor = color
    }

    fun setStroke(color: UIColor) {
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

    fun clip(to: CGRect) {
        canvas.clipRect(to.rectF)
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
        path = Path()
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

    fun closePath() {
        path.close()
    }

    // endregion
    fun fill(rect: CGRect) {
        param.fillMode()
        canvas.drawRect(rect.rectF, param.paint)
    }

    fun strokePath() {
        param.strokeMode()
        canvas.drawPath(path, param.paint)
    }

    fun fillPath() {
        param.fillMode()
        canvas.drawPath(path, param.paint)
    }

    // テキストを描画する
    // 引数のpointは左上
    fun drawText(text: String, point: CGPoint, font: UIFont, color: UIColor) {
        param.paint.textSize = font.pointSize.toFloat()
        param.fillColor = color
        param.fillMode()

        // canvas.drawTextの引数は左下なので、フォント高分下に下げる必要がある
        val height = "9".size(font).height
        canvas.drawText(text, point.x.toFloat(), point.y.toFloat() + height.toFloat(), param.paint)
    }

    fun drawTextInRect(text: String, rect: CGRect, font: UIFont, color: UIColor) {
        param.paint.textSize = font.pointSize.toFloat()
        param.fillColor = color
        param.fillMode()

        val height = "9".size(font).height
        canvas.drawText(text, rect.origin.x.toFloat(), rect.origin.y.toFloat() + height.toFloat(), param.paint)
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

        constructor() {
        }

        constructor(fillColor: UIColor?,
                    strokeColor: UIColor?,
                    lineDashPhase: Int?,
                    lineDashLengths: List<CGFloat>?,
                    shouldAntialias: Boolean) {
            this.fillColor = fillColor
            this.strokeColor = strokeColor
            this.lineDashPhase = lineDashPhase
            this.lineDashLengths = lineDashLengths
            this.shouldAntialias = shouldAntialias
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
        }

        fun clone(): Param {
            return Param(fillColor, strokeColor, lineDashPhase, lineDashLengths, shouldAntialias)
        }
    }
}