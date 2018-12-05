package kibaan.android.ui

import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import android.view.ViewOutlineProvider
import kibaan.android.ios.CGFloat
import kibaan.android.ios.UIColor

// ※APILevelが21以上であればViewOutlineProviderが使用できる
class ViewOutlineProcessor(val view: View) {

    // region -> Companion

    companion object {
        val path: Path by lazy {
            val path = Path()
            path.fillType = Path.FillType.INVERSE_EVEN_ODD
            return@lazy path
        }

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
                    view.outlineProvider = clipOutlineProvider
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

        }
    /** 枠線の太さ */
    var borderWidth: CGFloat = 0.0
        set(value) {
            field = value
            borderPaint.strokeWidth = value.toFloat()
        }
    /** 枠線描画用の[Paint] */
    private var borderPaint = Paint()
    /** APILevel21未満用にクリップ処理をする為の[Bitmap] */
    private var bitmap: Bitmap? = null
    /** クリアする対象の[RectF] */
    private val clearRect = RectF(0.0f, 0.0f, 0.0f, 0.0f)
    /** [ViewOutlineProvider]を使用可能かどうか */
    private val canUsedOutlineProvider: Boolean = Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT
    /** APILevel21以上用の[ViewOutlineProvider] */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val clipOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
        }
    }

    // endregion

    // region -> Initializer

    init {
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
    }

    // endregion

    // region -> Draw

    @Suppress("NAME_SHADOWING")
    private fun getBitmap(canvas: Canvas?): Bitmap? {
        val canvas = canvas ?: return null
        val bitmap = bitmap
        if (bitmap != null) {
            return bitmap
        }
        val width = if (view.isInEditMode) view.width else canvas.width
        val height = if (view.isInEditMode) view.height else canvas.height
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        return this.bitmap
    }

    fun getTmpCanvas(canvas: Canvas?): Canvas? {
        if (canUsedOutlineProvider) {
            return canvas
        }
        val bitmap = getBitmap(canvas) ?: return null
        return Canvas(bitmap)
    }

    @Suppress("NAME_SHADOWING")
    fun afterDraw(canvas: Canvas?, tmpCanvas: Canvas) {
        val canvas = canvas ?: return
        if (borderWidth != 0.0 && borderColor.intValue != Color.TRANSPARENT) {
            val borderWidth = borderWidth
            val halfWidth: Float = borderWidth.toFloat() / 2
            val rect = RectF(halfWidth, halfWidth, (canvas.width - halfWidth), (canvas.height - halfWidth))
            tmpCanvas.drawRoundRect(rect, radius.toFloat() - halfWidth, radius.toFloat() - halfWidth, borderPaint)
        }
        if (canUsedOutlineProvider) {
            return
        }
        val bitmap = getBitmap(canvas) ?: return
        clearRect.set(0.0f, 0.0f, canvas.width.toFloat(), canvas.height.toFloat())
        path.reset()
        path.addRoundRect(clearRect, radius.toFloat(), radius.toFloat(), Path.Direction.CW)
        tmpCanvas.drawPath(path, clearPaint)
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null)
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

