package kibaan.android.ui

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import kibaan.android.R
import kibaan.android.extension.dpToPx
import kibaan.android.extension.getStringOrNull
import kibaan.android.ios.*
import kibaan.android.util.DeviceUtils
import kibaan.android.util.EnumUtils
import kotlin.reflect.KClass

/**
 * セグメント状のボタン。セグメントの一つのボタンを選択状態にできる
 */
@IBDesignable
open class SegmentedButton : UIStackView {

    // region -> Constants

    /**
     * IBInspectable "names" の区切り文字
     */
    private val columnSeparator = ","

    /**
     * 未指定のサイズを表す数値
     */
    private val undefinedSize = -1

    enum class SegmentType(override val rawValue: Int) : IntEnum {
        CUSTOM(0), SQUARE(1), ROUNDED_SQUARE(2);

        companion object {
            fun find(byValue: Int?): SegmentType? {
                return values().find { it.rawValue == byValue }
            }
        }
    }
    // endregion

    // region -> Variables

    /**
     * ボタンと紐づく値のマップ
     */
    private var buttonGroup = ButtonGroup<String>()

    /**
     * タップ時の処理
     */
    private var tapHandler: ((String) -> Unit)? = null
    /**
     * タップ時の処理（タップ前の選択値付き）
     */
    private var tapHandlerWithOldValue: ((String?, String) -> Unit)? = null
    /**
     * スタイル
     */
    open var segmentType by didSet(SegmentType.CUSTOM) {
        if (!ViewCompat.isAttachedToWindow(this)) return@didSet
        constructSegments()
    }

    /** 角丸サイズ */
    open var segmentCornerRadius: Int = context.dpToPx(6.0)
        set(value) {
            field = value
            if (segmentType != SegmentType.CUSTOM) {
                setCornerRadius()
            }
        }
    /** ボタン間のスペース */
    open var horizontalSpacing: Int = context.dpToPx(1.0)
        set(value) {
            field = value
            updateHorizontalSpacing()
        }
    /** ボタン間の縦スペース */
    open var verticalSpacing: Int = context.dpToPx(1.0)
        set(value) {
            field = value
            updateVerticalSpacing()
        }

    // endregion

    // region -> Inspectable

    /**
     * テキストのフォントサイズ
     */
    @IBInspectable
    var textSize: CGFloat by didSet(context.dpToPx(12.0).toDouble()) {
        if (!ViewCompat.isAttachedToWindow(this)) return@didSet
        constructSegments()
    }

    /**
     * ボタンのテキストラベル。カンマ区切りで複数指定する
     */
    @IBInspectable
    var names: String by didSet("") {
        updateSegmentTitles()
    }

    /**
     * セグメントの列数
     */
    @IBInspectable
    var columnSize: Int by didSet(3) {
        if (!ViewCompat.isAttachedToWindow(this)) return@didSet
        constructSegments()
    }

    /**
     * セグメントの行数
     */
    @IBInspectable
    var rowSize: Int by didSet(1) {
        if (!ViewCompat.isAttachedToWindow(this)) return@didSet
        constructSegments()
    }
    /**
     * 非選択状態のボタン背景色
     */
    @IBInspectable open var normalButtonBackgroundColor: UIColor by didSet(UIColor.lightGray) {
        constructSegments()
    }
    /**
     * 選択状態のボタン背景色
     */
    @IBInspectable open var selectedButtonBackgroundColor: UIColor by didSet(UIColor(rgbValue = 0x0679FF)) {
        constructSegments()
    }
    /**
     * 非選択状態のボタン文字色
     */
    @IBInspectable open var normalButtonTextColor: UIColor by didSet(UIColor.white) {
        constructSegments()
    }
    /**
     * 選択状態のボタン文字色
     */
    @IBInspectable open var selectedButtonTextColor: UIColor by didSet(UIColor.white) {
        constructSegments()
    }
    /**
     * 非活性状態のボタン文字色
     */
    @IBInspectable open var disabledButtonTextColor: UIColor by didSet(UIColor.lightGray) {
        constructSegments()
    }

    var buttonCustomizer: ((SmartButton)->Unit)? = null

    /// 選択中のボタンに紐づく値
    val selectedValue: String?
        get() = buttonGroup.selectedValue

    /**
     * 選択されているインデックス
     */
    val selectedIndex: Int?
        get() {
            for (i in 0 until buttons.size) {
                if (buttons[i].isSelected) {
                    return i
                }
            }
            return null
        }

    /**
     * 選択されているボタンのタイトル
     */
    val selectedTitle: String?
        get() = buttons.firstOrNull { it.isSelected }?.currentTitle

    /// ボタンに紐づく値の一覧
    val values: List<String>
        get() = buttonGroup.values

    private var buttons: MutableList<SmartButton> = mutableListOf()

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setupSegmentButtons(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupSegmentButtons(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupSegmentButtons(context, attrs)
    }

    private fun setupSegmentButtons(context: Context, attrs: AttributeSet? = null) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SegmentedButton)
            columnSize = array.getInt(R.styleable.SegmentedButton_columnSize, columnSize)
            rowSize = array.getInt(R.styleable.SegmentedButton_rowSize, rowSize)
            segmentType = SegmentType.find(array.getInt(R.styleable.SegmentedButton_segmentType, SegmentType.CUSTOM.rawValue)) ?: SegmentType.CUSTOM
            textSize = array.getDimensionPixelSize(R.styleable.SegmentedButton_android_textSize, textSize.toInt()).toDouble()
            names = array.getStringOrNull(R.styleable.SegmentedButton_names) ?: names
            segmentCornerRadius = array.getDimensionPixelOffset(R.styleable.SegmentedButton_segmentCornerRadius, segmentCornerRadius)
            horizontalSpacing = array.getDimensionPixelOffset(R.styleable.SegmentedButton_horizontalSpacing, horizontalSpacing)
            verticalSpacing = array.getDimensionPixelOffset(R.styleable.SegmentedButton_verticalSpacing, verticalSpacing)
            normalButtonBackgroundColor = UIColor(array.getColor(R.styleable.SegmentedButton_normalButtonBackgroundColor, normalButtonBackgroundColor.intValue))
            selectedButtonBackgroundColor = UIColor(array.getColor(R.styleable.SegmentedButton_selectedButtonBackgroundColor, selectedButtonBackgroundColor.intValue))
            normalButtonTextColor = UIColor(array.getColor(R.styleable.SegmentedButton_normalButtonTextColor, normalButtonTextColor.intValue))
            selectedButtonTextColor = UIColor(array.getColor(R.styleable.SegmentedButton_selectedButtonTextColor, selectedButtonTextColor.intValue))
            disabledButtonTextColor = UIColor(array.getColor(R.styleable.SegmentedButton_disabledButtonTextColor, disabledButtonTextColor.intValue))
            array.recycle()
        }
        constructSegments()
    }

    init {
        axis = VERTICAL
    }

    // endregion

    // region -> Action

    fun actionSelect(button: UIButton) {
        val oldValue = buttonGroup.selectedValue
        buttonGroup.select(button = button)
        setSelectedButtonBold()
        executeCallback(oldValue = oldValue)
    }

    // endregion

    // region -> Getter

    /**
     * Enum型を指定して、選択中の値を取得する
     */
    fun <T : Enum<*>> selectedValue(type: KClass<T>): T? {
        return EnumUtils.getEnumValue(type, buttonGroup.selectedValue)
    }

    // endregion

    // region -> Setter

    /**
     * ボタン選択時の処理を設定する
     */
    fun <T : Enum<*>> onSelected(type: KClass<T>, callback: (T, Int) -> Unit) {
        tapHandler = { value ->
            val enumValue = EnumUtils.getEnumValue(type, value)

            if (enumValue != null) {
                callback(enumValue, this.selectedIndex ?: 0)
            }
        }
    }

    /**
     * ボタン選択時の処理を設定する
     */
    fun onSelected(callback: (String, Int) -> Unit) {
        tapHandler = { value ->
            callback(value, this.selectedIndex ?: 0)
        }
    }

    /**
     * ボタン選択時の処理を設定する（選択前の値が取得可能）
     */
    fun <T : Enum<*>> onSelectedWithOldValue(type: KClass<T>, callback: (T?, T, Int) -> Unit) {
        tapHandlerWithOldValue = { oldValue, value ->
            val enumValue = EnumUtils.getEnumValue(type, value)

            if (enumValue != null) {
                val oldEnumValue = EnumUtils.getEnumValue(type, oldValue)
                callback(oldEnumValue, enumValue, this.selectedIndex ?: 0)
            }
        }
    }

    /**
     * 各ボタンに紐づくEnum値を設定する
     */
    fun setEnumValues(values: Array<out Enum<*>>) {
        setEnumValues(values.toList())
    }

    /**
     * 各ボタンに紐づくEnum値を設定する
     */
    fun setEnumValues(values: List<Enum<*>>) {
        setValues(values.map { it.name })
    }

    /**
     * 各ボタンに紐づく値を設定する
     */
    fun setValues(values: List<String>) {
        buttonGroup.clear()
        values.enumerated().forEach {
            buttonGroup.register(buttons[it.offset], value = it.element)
        }
    }

    /**
     * 各紐づくEnum値と名前のセットを設定する
     */
    fun setEnumAndNames(values: List<Pair<Enum<*>, String>>) {
        setValueAndNames(values.map { Pair(it.first.name, it.second) })
    }

    fun setValueAndNames(values: List<Pair<String, String>>) {
        names = values.map { it.second }.joined(separator = ",")
        setValues(values.map { it.first })
    }

    /**
     * 紐づくEnum値のリストを取得する
     */
    fun <T : Enum<*>> getEnumValues(type: KClass<T>): List<T> {
        return buttonGroup.values.mapNotNull { EnumUtils.getEnumValue(type, it) }
    }

    /**
     * 指定した値に紐づくボタンの活性状態を設定する
     */
    fun <T : Enum<*>> setEnabled(value: T, enabled: Boolean) {
        setEnabled(value.name, enabled)
    }

    /**
     * 指定した値に紐づくボタンの活性状態を設定する
     */
    fun setEnabled(value: String, enabled: Boolean) {
        buttonGroup.get(value)?.isEnabled = enabled
    }

    /**
     * 指定したインデックスのボタンの活性状態を設定する
     */
    fun setEnabled(index: Int, enabled: Boolean) {
        if (index < buttons.count) {
            buttons[index].isEnabled = enabled
        }
    }

    /**
     * 指定した値に紐づくボタンを選択する
     */
    fun <T : Enum<T>> select(value: T, needCallback: Boolean = false) {
        select(value.name, needCallback)
    }

    /**
     * 指定した値に紐づくボタンを選択する
     */
    fun select(string: String, needCallback: Boolean = false) {
        val button = buttonGroup.get(string)
        if (button != null && button.isEnabled) {
            val oldValue = buttonGroup.selectedValue
            buttonGroup.select(button)
            setSelectedButtonBold()
            if (needCallback) {
                executeCallback(oldValue = oldValue)
            }
        }
    }

    /**
     * 指定したインデックスのボタンを選択する
     */
    fun select(index: Int, needCallback: Boolean = false) {
        val button = buttons[index]
        if (button.isEnabled) {
            val oldValue = buttonGroup.selectedValue
            buttonGroup.select(button = button)
            setSelectedButtonBold()
            if (needCallback) {
                executeCallback(oldValue = oldValue)
            }
        }
    }

    /**
     * 指定した値に紐づくボタンのタイトルを設定する
     */
    fun <T : Enum<T>> setTitle(title: String?, value: T) {
        setTitle(title, value = value.name)
    }

    /**
     * 指定した値に紐づくボタンのタイトルを設定する
     */
    fun setTitle(title: String?, value: String) {
        val button = buttonGroup.get(value)
        if (button is UIButton) {
            button.title = title
        }
    }

    /**
     * 指定した値に紐づくボタンの表示・非表示を切り替える
     */
    fun <T : Enum<T>> setHidden(hidden: Boolean, value: T) {
        setHidden(hidden, value = value.name)
    }

    /**
     * 指定した値に紐づくボタンの表示・非表示を切り替える
     */
    fun setHidden(hidden: Boolean, value: String) {
        val button = buttonGroup.get(value)
        if (button != null) {
            button.isEnabled = !hidden
            button.isHidden = hidden
        }
        if (segmentType != SegmentType.CUSTOM) {
            setCornerRadius()
        }
    }

    /**
     * 選択状態を解除する
     */
    fun clearSelection(needCallback: Boolean = false) {
        val oldValue = buttonGroup.selectedValue
        buttonGroup.selectedValue = null
        setSelectedButtonBold()
        if (needCallback) {
            executeCallback(oldValue = oldValue)
        }
    }

    // endregion

    // region -> Private

    /**
     * セグメントを作る
     */
    private fun constructSegments() {
        removeAllViews()
        if (columnSize == undefinedSize || rowSize == undefinedSize) {
            return
        }
        buttons.removeAll()
        for (i in 0 until rowSize) {
            val stackView = addHorizontalStackView()
            for (j in 0 until columnSize) {
                stackView.addArrangedSubview(createButton(j))
            }
        }
        updateSegmentTitles()
    }

    /**
     * セグメントのボタン名称を更新する
     */
    private fun updateSegmentTitles() {
        val nameList = names.components(separatedBy = columnSeparator)
        for (i in 0 until buttons.size) {
            val button = buttons[i]
            val isEnabled = i < nameList.size
            button.text = if (isEnabled) nameList[i] else ""
            button.visibility = if (isEnabled) View.VISIBLE else View.INVISIBLE
        }
        if (segmentType != SegmentType.CUSTOM) {
            setCornerRadius()
        }
    }

    /**
     * 行のスタックビューを追加する
     */
    private fun addHorizontalStackView(): UIStackView {
        val stackView = UIStackView(context)
        stackView.axis = LinearLayout.HORIZONTAL
        val params = LinearLayout.LayoutParams(MATCH_PARENT, 0, 1.0f)
        if (childCount != 0) {
            params.topMargin = verticalSpacing
        }
        stackView.layoutParams = params
        addView(stackView)
        return stackView
    }

    /**
     * 縦のスペースを更新する
     */
    fun updateVerticalSpacing() {
        arrangedSubviews.filterIndexed {index, view -> 0 < index}
                .forEach {
                    val param = it.layoutParams as? LinearLayout.LayoutParams
                    param?.topMargin = verticalSpacing
                }
    }

    /**
     * 横のスペースを更新する
     */
    fun updateHorizontalSpacing() {
        arrangedSubviews
                .mapNotNull { it as? UIStackView }
                .forEach {stackView ->
                    stackView.arrangedSubviews
                      .filterIndexed {index, view -> index < stackView.arrangedSubviews.size - 1}
                      .forEach {
                          val param = it.layoutParams as? LinearLayout.LayoutParams
                          param?.rightMargin = horizontalSpacing
                      }
                }
    }

    /**
     * 選択中のボタンのフォントをボールドにする
     */
    private fun setSelectedButtonBold() {
        buttons.forEach { it.setTypeface(null, if (it.isSelected) Typeface.BOLD else Typeface.NORMAL) }
    }

    /**
     * ボタンを作成する
     */
    private fun createButton(index: Int): UIButton {
        val button = makeButton()
        buttons.add(button)

        val params = LinearLayout.LayoutParams(0, MATCH_PARENT, 1f)
        val rightMargin = if (index < columnSize - 1) horizontalSpacing.toInt() else 0
        params.setMargins(0, 0, rightMargin, 0)
        button.layoutParams = params
        button.setBackgroundColor(normalButtonBackgroundColor, forState = UIControlState.normal)
        button.setBackgroundColor(selectedButtonBackgroundColor, forState = UIControlState.selected)
        button.setTitleColor(normalButtonTextColor, forState = UIControlState.normal)
        button.setTitleColor(selectedButtonTextColor, forState = UIControlState.selected)
        button.setTitleColor(disabledButtonTextColor, forState = UIControlState.disabled)
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
        button.adjustsFontSizeForWidth = true
        button.setOnClickListener {
            actionSelect(button)
        }
        buttonCustomizer?.invoke(button)
        return button
    }

    /**
     * ボタンを作成する
     */
    open fun makeButton(): SmartButton {
        return SmartButton(context)
    }

    /**
     * ボタンをカスタマイズする
     */
    open fun customizeButton(customizer: ((SmartButton) -> Unit)?) {
        buttonCustomizer = customizer
        buttons.forEach {
            customizer?.invoke(it)
        }
    }

    /**
     * コールバックを実行する
     */
    private fun executeCallback(oldValue: String?) {
        val newValue = buttonGroup.selectedValue
        if (newValue != null) {
            tapHandler?.invoke(newValue)
            tapHandlerWithOldValue?.invoke(oldValue, newValue)
        }
    }

    /**
     * 角丸を設定する
     */
    private fun setCornerRadius() {
        arrangedSubviews.mapNotNull { it as? UIStackView }.forEach {
            if (segmentType == SegmentType.ROUNDED_SQUARE) {
                it.cornerRadius = segmentCornerRadius
            } else {
                it.cornerRadius = 0
            }
        }
        // TODO:ButtonがHiddenになっている場合の考慮が必要（clipするpathを当該クラスで制御するなど、、、）
    }

}
