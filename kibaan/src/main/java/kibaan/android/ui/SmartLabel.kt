package kibaan.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import kibaan.android.R
import kibaan.android.extension.getStringOrNull
import kibaan.android.extension.isTrue
import kibaan.android.ios.*
import kibaan.android.util.DeviceUtils

/**
 * 共通テキストラベル
 */
open class SmartLabel : AppCompatTextView, SmartFontProtocol, ViewOutlineProcessable {

    // region -> Constants

    companion object {
        /** 太字のスタイル */
        private const val textStyleBold = 0b01
    }

    /** 自動縮小時の最小文字サイズ */
    protected val autoResizeMinTextSize = dpToPx(context, 4)

    // endregion

    // region -> Variables

    @Suppress("LeakingThis")
    override var viewOutlineProcessor: ViewOutlineProcessor = ViewOutlineProcessor(this)

    var isUserInteractionEnabled = true

    /** 横幅に合わせたフォントサイズ縮小前のフォントサイズ */
    private var rawTextSizePx: Float = textSize
    /** 自動縮小設定前の最大表示ライン */
    private var rawMaxLines: Int = maxLines
    /** 余白を考慮したテキストの表示領域 */
    private val textFrameWidth: Int get() = width - (paddingLeft + paddingRight)
    /** 描画済みかどうか */
    private var isLayoutCompleted = false
    /** 文字サイズの自動縮小をするかどうか */
    var adjustsFontSizeForWidth: Boolean = false
        set(value) {
            field = value
            super.setMaxLines(if (adjustsFontSizeForWidth) 1 else rawMaxLines)
            resizeFontForWidth()
        }
    /** フォント */
    var font: UIFont
        get() = originalFont
        set(value) {
            originalFont = value
            isBold = value.isBold
        }

    /** 太字かどうか */
    var isBold: Boolean = false
        set(value) {
            field = value
            setOriginalFontSize(size = originalFont.pointSize)
        }

    override var adjustsFontSizeForDevice: Boolean = false
        set(value) {
            field = value
            updateFont()
        }

    override var useGlobalFont: Boolean = true
        set(value) {
            field = value
            updateFont()
        }

    /** 端末サイズによるフォントサイズ調整とSmartContextのglobalFontを反映する前のフォント */
    private var originalFont: UIFont = UIFont(typeface, 17.0) // デフォルトで指定しているUIFontはコンストラクタで上書きされる為、使用されない
        set(value) {
            field = value
            updateFont()
        }

    /** 自動リサイズ用計算を行ったときの状態 */
    private var fontCalculatedSnapshot: StateSnapshot? = null
    /** 現在の状態 */
    private val stateSnapshot: StateSnapshot
        get() = StateSnapshot(
            text = text,
            baseTextSize = originalFont.pointSize.toFloat(),
            textFrameWidth = textFrameWidth,
            typeface = typeface
        )

    var textColor: UIColor?
        get() {
            return UIColor(currentTextColor)
        }
        set(value) {
            setTextColor(value?.intValue ?: Color.BLACK)
        }

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        commonInit(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        commonInit(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        commonInit(context, attrs)
    }

    private fun commonInit(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        var fontSizeUnit = FontSizeUnit.sp
        var isBold = false
        var adjustsFontSizeForDevice = this.adjustsFontSizeForDevice
        var useGlobalFont = this.useGlobalFont
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SmartLabel)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.SmartLabel_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.SmartLabel_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.SmartLabel_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.SmartLabel_isUserInteractionEnabled, isUserInteractionEnabled)
            adjustsFontSizeForWidth = array.getBoolean(R.styleable.SmartLabel_adjustsFontSizeForWidth, adjustsFontSizeForWidth)
            adjustsFontSizeForDevice = array.getBoolean(R.styleable.SmartLabel_adjustsFontSizeForDevice, adjustsFontSizeForDevice)
            useGlobalFont = array.getBoolean(R.styleable.SmartLabel_useGlobalFont, useGlobalFont)

            // textSizeの指定が"dp"か判定（指定がない場合は"sp"扱いとする）
            if (array.getStringOrNull(R.styleable.SmartLabel_android_textSize)?.hasSuffix("dp").isTrue) {
                fontSizeUnit = FontSizeUnit.dp
            }
            // android:textStyleがある場合はそちらを優先、なければisBoldを反映
            val textStyle = array.getInt(R.styleable.SmartLabel_android_textStyle, 0)
            isBold = (0 < textStyle and textStyleBold)
            array.recycle()
        }
        val textPointSize = if (fontSizeUnit == FontSizeUnit.sp) DeviceUtils.toSp(context, textSize.toInt()) else DeviceUtils.toDp(context, textSize.toInt())
        originalFont = if (isBold) {
            UIFont.boldSystemFont(textPointSize.toDouble(), fontSizeUnit)
        } else {
            UIFont.systemFont(textPointSize.toDouble(), fontSizeUnit)
        }
        // 以下の処理はデフォルトの"originalFont"が設定された後に実行する必要がある
        // 設定前に実行すると"didSet"で"updateFont"が呼ばれ、設定前のtextSizeで上書きされてしまう為（XMLに指定した値が無視される）
        this.adjustsFontSizeForDevice = adjustsFontSizeForDevice
        this.useGlobalFont = useGlobalFont

        setOnTouchListener { _, _ ->
            return@setOnTouchListener !isUserInteractionEnabled
        }
    }

    // SP単位のフォント指定
    override fun setTextSize(size: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    @Deprecated("Use font instead.")
    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        val pointSize = if (unit == TypedValue.COMPLEX_UNIT_PX) DeviceUtils.toSp(context, size.toInt()) else size
        setOriginalFontSize(size = pointSize.toDouble())
    }

    private fun setOriginalFontSize(size: CGFloat) {
        if (useGlobalFont) {
            originalFont = if (isBold) UIFont.boldSystemFont(ofSize = size) else UIFont.systemFont(ofSize = size)
        }
    }

    // endregion

    // region -> Life cycle

    /**
     * 画面表示時の処理
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        isLayoutCompleted = true
        resizeFontForWidth()
    }

    /**
     * テキスト内容変更時の処理
     */
    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        resizeFontForWidth()
    }

    // endregion

    // region -> Updater

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

    // endregion

    // region -> Support

    /**
     * DP値をPXに変換して返す
     */
    private fun dpToPx(context: Context, dp: Int): Float {
        return dp * context.resources.displayMetrics.density
    }

    // endregion

    // region -> SnapShot

    data class StateSnapshot(
        val text: CharSequence,
        val baseTextSize: Float,
        val textFrameWidth: Int,
        val typeface: Typeface
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
            result = 31 * result + typeface.hashCode()
            return result
        }

    }

    // endregion

    // region -> Functions

    private fun updateFont() {
        val convertedFont = convertFont(originalFont) ?: return
        rawTextSizePx = convertedFont.pointSize.toFloat()

        val pointSize = if (convertedFont.sizeUnit == FontSizeUnit.sp) {
            DeviceUtils.toPx(context, sp = convertedFont.pointSize.toFloat())
        } else {
            DeviceUtils.toPx(context, dp = convertedFont.pointSize)
        }.toFloat()

        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, pointSize)
        typeface = convertedFont.typeface
    }

    override fun setMaxLines(maxLines: Int) {
        super.setMaxLines(maxLines)
        rawMaxLines = maxLines
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        viewOutlineProcessor.draw(canvas) { super.draw(it) }
    }

    // endregion
}