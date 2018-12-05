package kibaan.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import kibaan.ios.UIColor
import kibaan.R

open class RoundedLinearLayout : LinearLayout, ViewOutlineProcessable {

    // region -> Variables

    @Suppress("LeakingThis")
    override var viewOutlineProcessor: ViewOutlineProcessor = ViewOutlineProcessor(this)

    var isUserInteractionEnabled = true

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setup(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)
    }
    // endregion

    // region -> Initializer

    private fun setup(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.RoundedLinearLayout)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.RoundedLinearLayout_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.RoundedLinearLayout_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.RoundedLinearLayout_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.RoundedLinearLayout_isUserInteractionEnabled, isUserInteractionEnabled)
            array.recycle()
        }
        setOnTouchListener { _, _ ->
            return@setOnTouchListener !isUserInteractionEnabled
        }
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        val tmpCanvas = viewOutlineProcessor.getTmpCanvas(canvas) ?: return
        super.draw(tmpCanvas)
        viewOutlineProcessor.afterDraw(canvas, tmpCanvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        val tmpCanvas = viewOutlineProcessor.getTmpCanvas(canvas) ?: return
        super.dispatchDraw(tmpCanvas)
        viewOutlineProcessor.afterDraw(canvas, tmpCanvas)
    }

    // endregion
}