package kibaan.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import kibaan.android.ios.UIColor
import kibaan.android.R

open class RoundedView : View, ViewOutlineProcessable {

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
            val array = context.obtainStyledAttributes(attrs, R.styleable.RoundedView)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.RoundedView_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.RoundedView_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.RoundedView_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.RoundedView_isUserInteractionEnabled, isUserInteractionEnabled)
            array.recycle()
        }
        setOnTouchListener { _, _ ->
            return@setOnTouchListener !isUserInteractionEnabled
        }
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        if (!viewOutlineProcessor.needsOutlineProcessing) {
            super.draw(canvas)
            return
        }
        val tempCanvas = viewOutlineProcessor.createTempCanvas(canvas)
        super.draw(tempCanvas)
        viewOutlineProcessor.afterDraw(canvas, tempCanvas)
    }

    // endregion
}