package kibaan.android.ui

import android.graphics.*
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import kibaan.android.ios.CGFloat
import kibaan.android.ios.UIColor

// ※APILevelが21以上であればViewOutlineProviderが使用できる
class ViewOutlineProcessor(val view: View) {

    // region -> Companion

    companion object {

        val clearPaint: Paint by lazy {
            val paint = Paint()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            return@lazy paint
        }
    }

    // endregion

    // region -> Variables

    /** 角丸半径 */
    var radius: Int = 0
        set(value) {
            field = value
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (0 < radius) {
                    view.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
                        }
                    }
                    view.clipToOutline = true
                } else {
                    view.outlineProvider = null
                    view.clipToOutline = false
                }
            } else {
                view.invalidate()
            }
        }
    /** 枠線の色 */
    var borderColor: UIColor = UIColor(Color.TRANSPARENT)
        set(value) {
            field = value
            borderPaint.color = value.intValue
            view.invalidate()

        }
    /** 枠線の太さ */
    var borderWidth: CGFloat = 0.0
        set(value) {
            field = value
            borderPaint.strokeWidth = value.toFloat()
            view.invalidate()
        }
    /** 枠線描画用の[Paint] */
    private var borderPaint = Paint()
    /** APILevel21未満用にクリップ処理をする為の[Bitmap] */
    private var bitmap: Bitmap? = null
    /** [ViewOutlineProvider]を使用可能かどうか */
    private val canUsedOutlineProvider: Boolean = Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT
    /** 独自でのアウトライン処理が必要かどうか */
    val needsOutlineProcessing: Boolean
        get() = !canUsedOutlineProvider && (0 < radius || 0.0 < borderWidth)

    // endregion

    // region -> Initializer

    init {
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
    }

    // endregion

    // region -> Draw

    fun draw(canvas: Canvas?, defaultDraw: ((Canvas?) -> Unit)) {
        val canvas = canvas ?: return
        val baseCanvas = getCanvas(canvas)
        defaultDraw(baseCanvas)

        if (needsOutlineProcessing) {
            clipRoundedRect(baseCanvas)
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
        }
        drawBorder(canvas)
    }

    private fun getCanvas(canvas: Canvas): Canvas {
        if (!needsOutlineProcessing) {
            return canvas
        }
        bitmap?.recycle()
        val width = if (view.isInEditMode) view.width else canvas.width
        val height = if (view.isInEditMode) view.height else canvas.height
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        return Canvas(bitmap)
    }

    private fun clipRoundedRect(canvas: Canvas) {
        val path = Path()
        path.fillType = Path.FillType.INVERSE_EVEN_ODD
        path.addRoundRect(
            RectF(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat()),
            radius.toFloat(), radius.toFloat(), Path.Direction.CW
        )
        canvas.drawPath(path, clearPaint)
    }

    @Suppress("NAME_SHADOWING")
    private fun drawBorder(canvas: Canvas?) {
        val canvas = canvas ?: return
        if (borderWidth != 0.0 && borderColor.intValue != Color.TRANSPARENT) {
            val borderWidth = borderWidth
            val halfWidth: Float = borderWidth.toFloat() / 2
            val rect = RectF(halfWidth, halfWidth, (canvas.width - halfWidth), (canvas.height - halfWidth))
            val rx = if (0 < radius) radius.toFloat() else 0.0f
            val ry = if (0 < radius) radius.toFloat() else 0.0f
            canvas.drawRoundRect(rect, rx, ry, borderPaint)
        }
    }

    // endregion
}

interface ViewOutlineProcessable {
    var viewOutlineProcessor: ViewOutlineProcessor
    var cornerRadius: Int
        get() = viewOutlineProcessor.radius
        set(value) {
            viewOutlineProcessor.radius = value
        }
    var borderColor: UIColor?
        get() = viewOutlineProcessor.borderColor
        set(value) {
            viewOutlineProcessor.borderColor = value ?: UIColor(Color.TRANSPARENT)
        }
    var borderWidth: CGFloat?
        get() = viewOutlineProcessor.borderWidth
        set(value) {
            viewOutlineProcessor.borderWidth = value ?: 0.0
        }
}

