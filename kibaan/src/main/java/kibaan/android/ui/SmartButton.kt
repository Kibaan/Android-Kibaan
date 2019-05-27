package kibaan.android.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter
import kibaan.android.R
import kibaan.android.extension.*
import kibaan.android.ios.*
import java.util.*
import kotlin.concurrent.schedule

@BindingAdapter("android:onTouchDown")
fun SmartButton.onTouchDown(listener: SmartButton.OnTouchDownListener) {
    this.onTouchDownListener = listener
}

/**
 * スマートな機能を持ったボタンクラス
 */
open class SmartButton : UIButton, View.OnTouchListener, SmartFontProtocol, ViewOutlineProcessable {

    // region -> Variables

    @Suppress("LeakingThis")
    override var viewOutlineProcessor: ViewOutlineProcessor = ViewOutlineProcessor(this)

    var isUserInteractionEnabled = true

    /** 長押しを有効とするか */
    var isEnableRepeat: Boolean = true

    var onTouchDownListener: OnTouchDownListener? = null

    /** 長押しを開始するまでの時間（ミリ秒） */
    var repeatDelay: Long = 500
    /** 長押しイベントの間隔（ミリ秒） */
    var repeatInterval: Long = 70
    /** 長押しの繰り返し用タイマー */
    private var repeatTimer: Timer? = null

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
            if (field != value) {
                field = value
                setOriginalFontSize(size = originalFont.pointSize)
            }
        }

    override var adjustsFontSizeForDevice = false
        set(value) {
            if (field != value) {
                field = value
                updateFont()
            }
        }

    override var useGlobalFont: Boolean = true
        set(value) {
            if (field != value) {
                field = value
                updateFont()
            }
        }

    /** 端末サイズによるフォントサイズ調整とSmartContextのglobalFontを反映する前のフォント */
    private var originalFont: UIFont = UIFont(typeface, 17.0) // デフォルトで指定しているUIFontはコンストラクタで上書きされる為、使用されない
        set(value) {
            field = value
            updateFont()
        }

    /** 各状態のRawValueとそれに紐づく背景色のMap */
    private var backgroundColorMap: MutableMap<UIControlState, UIColor?> = mutableMapOf()

    /** 選択中の背景色 */
    @IBInspectable var selectedBackgroundColor: UIColor? = null
        set(value) {
            setBackgroundColor(value, forState = UIControlState.selected)
        }
    /** 非活性時の背景色 */
    @IBInspectable var disabledBackgroundColor: UIColor? = null
        set(value) {
            setBackgroundColor(value, forState = UIControlState.disabled)
        }

    /** アイコンのサイズを決める為の基準の向き */
    private var iconBaseSide: IconBaseSide = IconBaseSide.HEIGHT

    /** アイコンの縮尺 */
    private var iconScale: CGFloat = 1.0

    /**
     * アイコンの縮尺
     * 例)横幅の0.5倍にする場合:"w:0.5"
     * 例)高さの0.8倍にする場合:"h:0.8"
     */
    @IBInspectable
    var iconScaleString: String? = null
        set(value) {
            if (value == field) return
            field = value
            if (value != null) {
                iconBaseSide = if (value.hasPrefix("w")) IconBaseSide.WIDTH else IconBaseSide.HEIGHT
                val array = value.components(separatedBy = ":")
                iconScale = array.last().toDouble()
                updateIconRect()
            }
        }


    /** アイコン描画用の[Paint] */
    private val iconPaint: Paint by lazy {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.isFilterBitmap = true
        return@lazy paint
    }

    /** 選択中のアイコン画像 */
    @IBInspectable
    var selectedIconImage: Bitmap? = null

    /** アイコン画像 */
    @IBInspectable
    var iconImage: Bitmap? by didSetWithOld<Bitmap?>(null) { oldValue ->
        if (oldValue != iconImage) {
            updateIconRect()
        }
    }

    /**  アイコンの上余白 */
    @IBInspectable var iconTopInset: CGFloat = 0.0
    /** アイコンの下余白 */
    @IBInspectable var iconBottomInset: CGFloat = 0.0
    /** アイコンの左余白 */
    @IBInspectable var iconLeftInset: CGFloat = 0.0
    /** アイコンの右余白 */
    @IBInspectable var iconRightInset: CGFloat = 0.0
    // TODO:消す必要がある（iOS版ではない）
    @IBInspectable var iconAspectRatio: CGFloat = 1.0
    /** アイコン描画用の元[Rect] */
    private var iconSrc: Rect? = null
    /** アイコン描画用の調整後[Rect] */
    private var iconDst: Rect? = null
    /** タッチの排他時間 */
    open var touchExclusiveDuration: Long? = null

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

    // endregion

    // region -> Initializer

    private fun commonInit(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        var fontSizeUnit = SmartFontProtocol.defaultFontSizeUnit
        var adjustsFontSizeForDevice = this.adjustsFontSizeForDevice
        var useGlobalFont = this.useGlobalFont
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SmartButton)
            cornerRadius = array.getDimensionPixelOffset(R.styleable.SmartButton_cornerRadius, 0)
            borderColor = UIColor(array.getColor(R.styleable.SmartButton_borderColor, Color.TRANSPARENT))
            borderWidth = array.getDimensionPixelOffset(R.styleable.SmartButton_borderWidth, 0).toDouble()
            isUserInteractionEnabled = array.getBoolean(R.styleable.SmartButton_isUserInteractionEnabled, isUserInteractionEnabled)
            adjustsFontSizeForDevice = array.getBoolean(R.styleable.SmartButton_adjustsFontSizeForDevice, adjustsFontSizeForDevice)
            useGlobalFont = array.getBoolean(R.styleable.SmartButton_useGlobalFont, useGlobalFont)
            isEnableRepeat = array.getBoolean(R.styleable.SmartButton_enableRepeat, true)
            iconScaleString = array.getStringOrNull(R.styleable.SmartButton_iconScale)
            iconTopInset = array.getFloat(R.styleable.SmartButton_iconTopInset, 0.0f).toDouble()
            iconBottomInset = array.getFloat(R.styleable.SmartButton_iconBottomInset, 0.0f).toDouble()
            iconLeftInset = array.getFloat(R.styleable.SmartButton_iconLeftInset, 0.0f).toDouble()
            iconRightInset = array.getFloat(R.styleable.SmartButton_iconRightInset, 0.0f).toDouble()
            iconAspectRatio = array.getFloat(R.styleable.SmartButton_iconAspectRatio, 1.0f).toDouble()
            if (array.hasValue(R.styleable.SmartButton_iconImage)) {
                iconImage = BitmapFactory.decodeResource(resources, array.getResourceId(R.styleable.SmartButton_iconImage, -1))
            }
            if (array.hasValue(R.styleable.SmartButton_selectedIconImage)) {
                selectedIconImage = BitmapFactory.decodeResource(resources, array.getResourceId(R.styleable.SmartButton_selectedIconImage, -1))
            }

            // 状態毎の背景色の設定
            val backgroundColor = array.getUIColorOrNull(R.styleable.SmartButton_android_background)
            if (backgroundColor != null) {
                backgroundColorMap[UIControlState.normal] = backgroundColor
                backgroundColorMap[UIControlState.highLighted] = backgroundColor
                backgroundColorMap[UIControlState.selected] = backgroundColor
                backgroundColorMap[UIControlState.disabled] = backgroundColor
            }
            if (array.hasValue(R.styleable.SmartButton_selectedBackgroundColor)) {
                backgroundColorMap[UIControlState.selected] = UIColor(array.getColor(R.styleable.SmartButton_selectedBackgroundColor, Color.TRANSPARENT))
            }
            if (array.hasValue(R.styleable.SmartButton_disabledBackgroundColor)) {
                backgroundColorMap[UIControlState.disabled] = UIColor(array.getColor(R.styleable.SmartButton_disabledBackgroundColor, Color.TRANSPARENT))
            }
            updateBackgroundColor()

            // textSize単位の指定
            val textSizeAttribute = array.getStringOrNull(R.styleable.SmartLabel_android_textSize)
            if (textSizeAttribute?.hasSuffix("dip").isTrue) {
                fontSizeUnit = FontSizeUnit.dp
            } else if (textSizeAttribute?.hasSuffix("sp").isTrue) {
                fontSizeUnit = FontSizeUnit.sp
            }
            array.recycle()
        }
        val textPointSize = if (fontSizeUnit == FontSizeUnit.sp) context.pxToSp(textSize) else context.pxToDp(textSize)
        originalFont = if (isBold) {
            UIFont.boldSystemFont(textPointSize.toDouble(), fontSizeUnit)
        } else {
            UIFont.systemFont(textPointSize.toDouble(), fontSizeUnit)
        }
        // 以下の処理はデフォルトの"originalFont"が設定された後に実行する必要がある
        // 設定前に実行すると"didSet"で"updateFont"が呼ばれ、設定前のtextSizeで上書きされてしまう為（XMLに指定した値が無視される）
        this.adjustsFontSizeForDevice = adjustsFontSizeForDevice
        this.useGlobalFont = useGlobalFont

        setOnTouchListener(this)
    }

    // endregion

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener {
            if (!TouchLock.canTouch) {
                return@setOnClickListener
            }

            listener?.onClick(this)

            val duration = touchExclusiveDuration
            if (duration != null && 0 < duration) {
                TouchLock.lock(duration)
            }
        }
    }

    // region -> Life cycle

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateIconRect()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateIconRect()
    }

    // endregion

    // region -> Icon Variables & IBInspectable & Methods

    /**
     * アイコンの[Rect]を更新する
     */
    private fun updateIconRect() {
        val icon = iconImage ?: return
        if (width == 0 && height == 0) return

        val iconWidth: CGFloat
        val iconHeight: CGFloat
        if (iconBaseSide == IconBaseSide.WIDTH) {
            iconWidth = width * iconScale
            iconHeight = iconWidth * iconAspectRatio
        } else {
            iconHeight = height * iconScale
            iconWidth = iconHeight * iconAspectRatio
        }
        val verticalInset = ((iconTopInset - iconBottomInset) * height).toInt()
        val horizontalInset = ((iconLeftInset - iconRightInset) * width).toInt()
        val dstLeft = (width / 2 - iconWidth / 2 + horizontalInset).toInt()
        val dstTop = (height / 2 - iconHeight / 2 + verticalInset).toInt()
        iconSrc = Rect(0, 0, icon.width, icon.height)
        iconDst = Rect(dstLeft, dstTop, width - dstLeft + horizontalInset * 2, height - dstTop + verticalInset * 2)
    }

    // endregion

    // region -> Repeat

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownAction()
                startRepeat()
            }
            MotionEvent.ACTION_MOVE -> {
                if (repeatTimer != null) {
                    return isUserInteractionEnabled // 長押し処理中は他にイベントを通知しない
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                removeTimer()
            }
        }

        return !isUserInteractionEnabled
    }

    private fun touchDownAction() {
        onTouchDownListener?.onTouchDown(this)
    }

    private fun startRepeat() {
        if (isEnableRepeat && repeatTimer == null) {
            repeatTimer = Timer()
            repeatTimer?.schedule(repeatDelay, repeatInterval) {
                repeatAction()
            }
        }
    }

    private fun repeatAction() {
        handler?.post {
            touchDownAction()
        }
    }

    private fun removeTimer() {
        repeatTimer?.cancel()
        repeatTimer = null
    }

    // endregion

    // region -> Setter

    // SP単位のフォント指定
    override fun setTextSize(size: Float) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    @Deprecated("Use font instead.")
    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)

        val pointSize = if (unit == TypedValue.COMPLEX_UNIT_PX) context.pxToSp(size) else size
        setOriginalFontSize(pointSize.toDouble())
    }

    private fun setOriginalFontSize(size: CGFloat) {
        if (useGlobalFont) {
            originalFont = if (isBold) UIFont.boldSystemFont(ofSize = size) else UIFont.systemFont(ofSize = size)
        }
    }

    /**
     * 指定したステータスに背景色を設定する
     */
    fun setBackgroundColor(color: UIColor?, forState: UIControlState) {
        backgroundColorMap[forState] = color
        updateBackgroundColor()
    }

    override fun setBackgroundColor(color: Int) {
        setBackgroundColor(UIColor(color), forState = UIControlState.normal)
    }

    // endregion

    // region -> Getter

    /**
     * 指定したステータスの背景色を返す
     */
    fun backgroundColor(forState: UIControlState): UIColor? {
        return backgroundColorMap[forState]
    }

    // endregion

    // region -> Draw

    override fun draw(canvas: Canvas?) {
        viewOutlineProcessor.draw(canvas) { super.draw(it) }
        drawIcon(canvas = canvas)
    }

    private fun drawIcon(canvas: Canvas?) {
        val icon = if (isSelected && selectedIconImage != null) selectedIconImage else iconImage
        if (icon != null) {
            if (iconSrc == null || iconDst == null) {
                updateIconRect()
            }
            canvas?.drawBitmap(icon, iconSrc, iconDst, iconPaint)
        }
    }

    // endregion

    // region -> Functions

    private fun updateFont() {
        val convertedFont = convertFont(originalFont) ?: return
        val pxSize = if (convertedFont.sizeUnit == FontSizeUnit.sp) {
            context.spToPx(convertedFont.pointSize)
        } else {
            context.dpToPx(convertedFont.pointSize)
        }.toFloat()
        rawTextSizePx = pxSize

        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxSize)
        typeface = convertedFont.typeface
    }

    /**
     * 背景色を更新する
     */
    private fun updateBackgroundColor() {
        if (backgroundColorMap.isEmpty()) return

        val stateListDrawable = StateListDrawable()
        backgroundColorMap.forEach {
            val colorValue = it.value?.intValue ?: return@forEach
            stateListDrawable.addState(it.key.states, ColorDrawable(colorValue))
        }
        background = stateListDrawable
    }

    // endregion

    interface OnTouchDownListener {
        fun onTouchDown(view: View)
    }

    /**
     * アイコンのサイズを決める為の基準の向き
     */
    enum class IconBaseSide {
        HEIGHT, WIDTH;
    }

}