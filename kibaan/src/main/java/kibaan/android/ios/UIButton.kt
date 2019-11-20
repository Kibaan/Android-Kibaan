package kibaan.android.ios

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import kibaan.android.R
import kibaan.android.extension.getBitmapOrNull
import kibaan.android.extension.getUIColorOrNull

/**
 *
 * Created by Yamamoto Keita on 2018/01/21.
 */
open class UIButton : AppCompatButton {

    private val autoResizeMinTextSize = dpToPx(context, 4)
    private val imageMap = mutableMapOf<Int, Bitmap?>()
    private var fontCalculatedSnapshot: StateSnapshot? = null

    @IBInspectable
    var adjustsFontSizeForWidth: Boolean = false
        set(value) {
            field = value
            super.setMaxLines(if (adjustsFontSizeForWidth) 1 else rawMaxLines)
            resizeFontForWidth()
        }

    var defaultTextColor: UIColor? = null
        set(value) {
            field = value
            updateTextColor()
        }

    var highlightedTextColor: UIColor? = null
        set(value) {
            field = value
            updateTextColor()
        }

    var selectedTextColor: UIColor? = null
        set(value) {
            field = value
            updateTextColor()
        }

    var disabledTextColor: UIColor? = null
        set(value) {
            field = value
            updateTextColor()
        }

    var defaultImage: Bitmap? = null
        set(value) {
            field = value
            setImageBitmap(bitmap = value, state = UIControlState.normal.rawValue)
        }

    var highlightedImage: Bitmap? = null
        set(value) {
            field = value
            setImageBitmap(bitmap = value, state = UIControlState.highLighted.rawValue)
        }

    var selectedImage: Bitmap? = null
        set(value) {
            field = value
            setImageBitmap(bitmap = value, state = UIControlState.selected.rawValue)
        }

    var disabledImage: Bitmap? = null
        set(value) {
            field = value
            setImageBitmap(bitmap = value, state = UIControlState.disabled.rawValue)
        }

    private var isLayoutCompleted = false

    /** 横幅に合わせたフォントサイズ縮小前のフォントサイズ */
    var rawTextSizePx: Float = textSize
    /** 自動縮小設定前の最大表示ライン */
    private var rawMaxLines: Int = maxLines

    // region -> computed property

    var title: String?
        get() = text.toString()
        set(value) {
            text = value
        }

    var titleColor: UIColor?
        get() = titleColor(UIControlState.normal)
        set(value) {
            setTitleColor(value, forState = UIControlState.normal)
        }

    private val textFrameWidth: Int
        get() = width - (paddingLeft + paddingRight)

    /** WidthがWrapContentかどうか */
    private val isWrapContent: Boolean
        get() {
            val layoutParams = this.layoutParams ?: return false
            return layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT
        }

    private val stateSnapshot: StateSnapshot
        get() = StateSnapshot(
            text = text,
            baseTextSize = rawTextSizePx,
            textFrameWidth = textFrameWidth,
            typeface = typeface
        )

    val currentTitle: String?
        get() = title

    // endregion

    constructor(context: Context) : super(context) {
        setupUIButton(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupUIButton(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupUIButton(context, attrs)
    }

    // region -> Initializer

    private fun setupUIButton(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UIButton)
            defaultTextColor = array.getUIColorOrNull(R.styleable.UIButton_defaultTextColor)
            highlightedTextColor = array.getUIColorOrNull(R.styleable.UIButton_highlightedTextColor)
            selectedTextColor = array.getUIColorOrNull(R.styleable.UIButton_selectedTextColor)
            disabledTextColor = array.getUIColorOrNull(R.styleable.UIButton_disabledTextColor)

            defaultImage = array.getBitmapOrNull(R.styleable.UIButton_defaultImage)
            highlightedImage = array.getBitmapOrNull(R.styleable.UIButton_highlightedImage)
            selectedImage = array.getBitmapOrNull(R.styleable.UIButton_selectedImage)
            disabledImage = array.getBitmapOrNull(R.styleable.UIButton_disabledImage)

            array.recycle()
        }
    }

    // endregion

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isLayoutCompleted = true
        resizeFontForWidth()
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)

        if (adjustsFontSizeForWidth && isWrapContent && !isInEditMode) {
            // テキストが変わったらWrapContentの場合は、一度フォントサイズをリセットする必要がある
            if (fontCalculatedSnapshot == null || fontCalculatedSnapshot != stateSnapshot) {
                super.setTextSize(TypedValue.COMPLEX_UNIT_PX, rawTextSizePx)
            }
        } else {
            resizeFontForWidth()
        }
    }

    fun setTitleColor(color: UIColor?, forState: UIControlState) {
        when (forState) {
            UIControlState.normal -> defaultTextColor = color
            UIControlState.highLighted -> highlightedTextColor = color
            UIControlState.selected -> selectedTextColor = color
            UIControlState.disabled -> disabledTextColor = color
        }
    }

    fun titleColor(forState: UIControlState): UIColor? {
        return when (forState) {
            UIControlState.normal -> defaultTextColor
            UIControlState.highLighted -> highlightedTextColor
            UIControlState.selected -> selectedTextColor
            UIControlState.disabled -> disabledTextColor
        }
    }

    fun setImage(@DrawableRes imageId: Int, state: UIControlState) {
        setImage(imageId, state.rawValue)
    }

    fun setImage(@DrawableRes imageId: Int, state: Int) {
        setImageBitmap(BitmapFactory.decodeResource(resources, imageId), state = state)
    }

    fun setImageBitmap(bitmap: Bitmap?, state: Int) {
        imageMap[state] = bitmap
        invalidate()
    }

    fun setBackgroundImage(@DrawableRes imageId: Int, state: UIControlState) {
        setImage(imageId, state)
    }

    fun setBackgroundImage(@DrawableRes imageId: Int, state: Int) {
        setImage(imageId, state)
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        rawMaxLines = maxLines
    }

    /**
     * フォントサイズを横幅に合わせて調整する
     */
    private fun resizeFontForWidth() {
        if (isLayoutCompleted && adjustsFontSizeForWidth &&
            (fontCalculatedSnapshot == null || fontCalculatedSnapshot != stateSnapshot) /* 前回リサイズ時と状態が異なる場合のみ再設定 */) {
            val size = FontUtils.adjustTextSize(
                text = text.toString(), baseTextSize = rawTextSizePx,
                width = textFrameWidth, typeface = typeface, minSize = autoResizeMinTextSize
            )
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)

            fontCalculatedSnapshot = stateSnapshot
        }
    }

    /**
     * 文字色の更新
     */
    private fun updateTextColor() {
        val defaultTextColor = this.defaultTextColor ?: UIColor(textColors.defaultColor)
        val highlightedTextColor = this.highlightedTextColor ?: defaultTextColor
        val selectedTextColor = this.selectedTextColor ?: defaultTextColor
        val disabledTextColor = this.disabledTextColor ?: defaultTextColor
        val states = arrayOf(
            UIControlState.normal.states,
            UIControlState.highLighted.states,
            UIControlState.selected.states,
            UIControlState.disabled.states
        )
        val colors = intArrayOf(
            defaultTextColor.intValue,
            highlightedTextColor.intValue,
            selectedTextColor.intValue,
            disabledTextColor.intValue
        )
        setTextColor(ColorStateList(states, colors))
    }

    private fun dpToPx(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }

    data class StateSnapshot(
        val text: CharSequence,
        val baseTextSize: Float,
        val textFrameWidth: Int,
        val typeface: Typeface?
    ) {
        @Suppress("NAME_SHADOWING")
        override operator fun equals(other: Any?): Boolean {
            val other = (other as? StateSnapshot) ?: return false
            return text == other.text
                    && baseTextSize == other.baseTextSize
                    && textFrameWidth == other.textFrameWidth
                    && typeface == other.typeface
        }

        override fun hashCode(): Int {
            var result = text.hashCode()
            result = 31 * result + baseTextSize.hashCode()
            result = 31 * result + textFrameWidth
            result = 31 * result + (typeface?.hashCode() ?: 0)
            return result
        }
    }

    // region -> Private

    private fun getTargetImageState(): Int {
        val normalValue = UIControlState.normal.rawValue
        val highLightedValue = UIControlState.highLighted.rawValue
        val disabledValue = UIControlState.disabled.rawValue
        val selectedValue = UIControlState.selected.rawValue
        var state = 0
        state += if (isPressed) highLightedValue else 0
        state += if (!isEnabled) disabledValue else 0
        state += if (isSelected) selectedValue else 0
        if (!imageMap.contains(state)) {
            if (isSelected && imageMap.contains(selectedValue)) {
                return selectedValue
            } else if (!isEnabled && imageMap.contains(disabledValue)) {
                return disabledValue
            } else if (isPressed && imageMap.contains(highLightedValue)) {
                return highLightedValue
            }
            return normalValue
        }
        return state
    }

    private fun getImageBitmap(): Bitmap? {
        return imageMap[getTargetImageState()]
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val canvas = canvas ?: return
        val bitmap = getImageBitmap() ?: return
        val src = Rect(0, 0, bitmap.width, bitmap.height)
        val dst = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, src, dst, Paint())
    }

    // endregion
}