package kibaan.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.widget.RelativeLayout
import kibaan.android.ios.UIColor
import kibaan.android.R

open class RoundedRelativeLayout : RelativeLayout, ViewOutlineProcessable {

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
            val array = context.obtainStyledAttributes(attrs, R.styleable.RoundedRelativeLayout)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.RoundedRelativeLayout_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.RoundedRelativeLayout_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.RoundedRelativeLayout_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.RoundedRelativeLayout_isUserInteractionEnabled, isUserInteractionEnabled)
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

    // endregion
}