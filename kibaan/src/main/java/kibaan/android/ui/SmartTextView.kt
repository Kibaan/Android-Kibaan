package kibaan.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import android.widget.ScrollView
import kibaan.android.extension.getStringOrNull
import kibaan.android.ios.UIColor
import kibaan.android.ios.didSet
import kibaan.android.util.DeviceUtils
import kibaan.android.R

open class SmartTextView : ScrollView, ViewOutlineProcessable {

    // region -> Variables

    @Suppress("LeakingThis")
    override var viewOutlineProcessor: ViewOutlineProcessor = ViewOutlineProcessor(this)

    var isUserInteractionEnabled = true

    private var frame = FrameLayout(context)

    private var textView = LinkLabel(context)

    var text: String? by didSet(null) {
        textView.text = text
        scrollTo(0, 0)
    }

    var contentPaddingBottom: Int by didSet(0) {
        contentPaddingBottomPx = DeviceUtils.toPx(context, contentPaddingBottom)
        updatePadding()
    }
    var contentPaddingTop: Int by didSet(0) {
        contentPaddingTopPx = DeviceUtils.toPx(context, contentPaddingTop)
        updatePadding()
    }
    var contentPaddingLeft: Int by didSet(0) {
        contentPaddingLeftPx = DeviceUtils.toPx(context, contentPaddingLeft)
        updatePadding()
    }
    var contentPaddingRight: Int by didSet(0) {
        contentPaddingRightPx = DeviceUtils.toPx(context, contentPaddingRight)
        updatePadding()
    }

    private var contentPaddingBottomPx: Int = 0
    private var contentPaddingTopPx: Int = 0
    private var contentPaddingLeftPx: Int = 0
    private var contentPaddingRightPx: Int = 0

    var adjustsFontSizeForDevice: Boolean
        get() = textView.adjustsFontSizeForDevice
        set(value) {
            textView.adjustsFontSizeForDevice = value
        }

    var useGlobalFont: Boolean
        get() = textView.useGlobalFont
        set(value) {
            textView.useGlobalFont = value
        }

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
        addTextView()
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SmartTextView)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.SmartTextView_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.SmartTextView_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.SmartTextView_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.SmartTextView_isUserInteractionEnabled, isUserInteractionEnabled)
            adjustsFontSizeForDevice = array.getBoolean(R.styleable.SmartTextView_adjustsFontSizeForDevice, adjustsFontSizeForDevice)
            useGlobalFont = array.getBoolean(R.styleable.SmartTextView_useGlobalFont, useGlobalFont)
            text = array.getStringOrNull(R.styleable.SmartTextView_text) ?: ""
            contentPaddingBottom = array.getDimensionPixelOffset(R.styleable.SmartTextView_content_paddingBottom, 0)
            contentPaddingTop = array.getDimensionPixelOffset(R.styleable.SmartTextView_content_paddingTop, 0)
            contentPaddingLeft = array.getDimensionPixelOffset(R.styleable.SmartTextView_content_paddingLeft, 0)
            contentPaddingRight = array.getDimensionPixelOffset(R.styleable.SmartTextView_content_paddingRight, 0)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, array.getDimension(R.styleable.SmartTextView_android_textSize, DeviceUtils.toPx(context, 14).toFloat()))
            array.recycle()
        }
        setOnTouchListener { _, _ ->
            return@setOnTouchListener !isUserInteractionEnabled
        }
    }

    private fun addTextView() {
        addView(frame)
        frame.addView(textView)
    }

    // endregion

    // region -> Methods

    private fun updatePadding() {
        frame.setPadding(contentPaddingLeftPx, contentPaddingTopPx, contentPaddingRightPx, contentPaddingBottomPx)
    }

    fun scrollToTop(animated: Boolean = false) {
        if (animated) {
            smoothScrollTo(0, 0)
        } else {
            scrollY = 0
        }
    }

    fun setLinks(linkList: List<LinkLabel.LinkInfo>, color: UIColor) {
        textView.setLinks(linkList, color = color)
    }

    fun setTypeface(typeface: Typeface) {
        textView.typeface = typeface
    }

    fun setTextSize(unit: Int, size: Float) {
        textView.setTextSize(unit, size)
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        viewOutlineProcessor.draw(canvas) { super.draw(it) }
    }

    // endregion
}