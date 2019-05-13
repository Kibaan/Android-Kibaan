package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import kibaan.android.AndroidUnique
import kibaan.android.R
import kibaan.android.extension.getStringOrNull
import kibaan.android.extension.width
import kibaan.android.ios.*
import kotlin.math.abs


/// 無限スクロールするセグメントボタン群
/// - 全ボタンが横幅に収まりきる場合は、隙間が空かないよう全ボタンを均等配置して、スクロールを無効にする
/// - ループを考慮して1つダミーのボタンを作る（左右端どちらにも同じボタンが見える場合があるため）
class ScrollSegmentedButton: HorizontalScrollView {

    @IBInspectable var textSize: CGFloat = 14.0
    @IBInspectable var scrollButtonWidth: CGFloat = 100.0
    @IBInspectable var titlesText: String
        get() {
            return titles.joined(separator = ",")
        }
        set(value) {
            titles = value.components(separatedBy = ",").toMutableList()
        }
    @IBInspectable var buttonCount: Int = 0
        set(newValue) {
            val oldValue = field
            field = newValue
            setup(buttonCount = buttonCount)
        }

    /// 実際に表示するボタンの横幅
    val buttonWidth: CGFloat
        get() = if (isFitButtons) (frame.width / titles.size) else scrollButtonWidth

    /// 選択中のインデックス
    var selectedIndex: Int?
        get() {
            if (titles.size <= buttons.size) {

                return buttons.subList(fromIndex = 0, toIndex = titles.size - 1)
                    .enumerated()
                    .firstOrNull{ it.element.isSelected }?.offset
            }
            return 0
        }
        set(value) {
            select(value, needsCallback = true)
        }
    var titles: MutableList<String> = mutableListOf()
        set(newValue) {
            field = newValue
            updateButtonTitles()
        }
    var buttons: List<UIButton> = listOf()

    var onSelected: ((oldIndex: Int?, index: Int) -> Unit)? = null

    /// ダミーボタン
    private var dummyButton: UIButton = UIButton(context)
    /// 直前のサイズ
    /// サイズが変わった場合にページサイズや位置の調整が必要なため、サイズ変更が分かるよう直前のサイズを保存している
    private var previousSize: CGSize? = null

    /// 余白部分に移動するボタン個数
    private val marginCount: Int
        get() = (frame.width / scrollButtonWidth).toInt()

    /// ボタンが端末の横幅に収まるか
    private val isFitButtons: Boolean
        get() = titles.size * scrollButtonWidth <= frame.width

    /// 左端に表示しているボタン
    private val leftEndButton: UIButton?
        get() = buttons.sorted(by = { lhs, rhs  ->
            lhs.frame.minX < rhs.frame.minX
        }).firstOrNull()

    /// ボタンの見た目を更新する為の処理
    private var buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null

    @AndroidUnique
    var isScrollEnabled = true

    @AndroidUnique
    val content = FrameLayout(context)

    constructor(context: Context) : super(context) {
        setup(context)
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup(context, attrs)
    }

    private fun setup(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ScrollSegmentedButton)

            // TODO サイズをpixelで保持してよいのか？
            textSize = array.getDimensionPixelSize(R.styleable.ScrollSegmentedButton_android_textSize, textSize.toInt()).toDouble()
            scrollButtonWidth = array.getDimensionPixelSize(R.styleable.ScrollSegmentedButton_scrollButtonWidth, scrollButtonWidth.toInt()).toDouble()

            titlesText = array.getStringOrNull(R.styleable.ScrollSegmentedButton_titlesText) ?: titlesText
            buttonCount = array.getInt(R.styleable.ScrollSegmentedButton_buttonCount, 0)

            array.recycle()
        }

        addView(content, RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    init {
        isHorizontalScrollBarEnabled = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // サイズが変わった場合
        if (previousSize == null || previousSize != frame.size) {
            isScrollEnabled = !isFitButtons
            updateScrollSize()
            updateButtonSize()
            moveToCenter(animated = false)
            updateButtonPosition()
            previousSize = frame.size
        }
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return isScrollEnabled
    }

//    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        if (isScrollEnabled) {
//            return super.onInterceptTouchEvent(ev)
//        }
//        return false
//    }
//
//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        if (isScrollEnabled) {
//            return super.onTouchEvent(ev)
//        }
//        return false
//    }

    // MARK: - Initializer
    fun setup(buttonCount: Int, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.buttonUpdater = buttonUpdater
        makeButtons(buttonCount = buttonCount, buttonMaker = buttonMaker ?: this::makeDefaultButton)
    }

    fun setup(titles: List<String>, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.titles = titles.toMutableList()
        this.buttonUpdater = buttonUpdater
        makeButtons(buttonCount = titles.size, buttonMaker = buttonMaker ?: this::makeDefaultButton)
    }

    fun makeDefaultButton() : UIButton {
        val button = SmartButton(context)
        button.textSize = textSize.toFloat() // TODO 単位が合っているか確認
        button.setTitleColor(UIColor.lightGray, forState = UIControlState.normal)
        button.setTitleColor(UIColor.white, forState = UIControlState.selected)
        button.adjustsFontSizeForWidth = true
        return button
    }

    fun defaultButtonStateUpdater(button: UIButton, buttonState: ButtonState) {
        val button = button as? SmartButton ?: return
        when (buttonState) {
            ButtonState.unselected -> button.isBold = false
            ButtonState.selected -> button.isBold = true
        }
    }

    fun makeButtons(buttonCount: Int, buttonMaker: (() -> UIButton)) {
        clearView()
        buttons = (0 until buttonCount).map {
            val button = buttonMaker()
            content.addView(button)
            button.setOnClickListener {
                actionSelect(button)
            }
            button
        }
        dummyButton = buttonMaker()
        content.addView(dummyButton)
        updateButtonTitles()
        updateButtonPosition()
        isScrollEnabled = !isFitButtons
    }

    // MARK: - Others
    fun select(index: Int?, animated: Boolean = true, needsCallback: Boolean = true) {
        val index = index ?: return
        buttons.forEach { it.isSelected = false }
        buttons.safeGet(index)?.isSelected = true
        updateButton()
        if (isScrollEnabled) {
            moveToCenter(animated = animated)
        }
    }

    fun clear() {
        buttonCount = 0
        titles.removeAll()
        clearView()
    }

    fun clearView() {
        previousSize = null
        content.removeAllViews()
    }

    // MARK: - Action
    private fun actionSelect(button: UIButton) {
        val oldIndex = selectedIndex
        buttons.forEach { it.isSelected = false }
        if (isDummyButton(button)) {
            leftEndButton?.isSelected = true
        } else {
            button.isSelected = true
        }
        updateButton()
        if (isScrollEnabled) {
            moveToCenter(animated = true)
        }
        val index = selectedIndex
        if (index != null) {
            onSelected?.invoke(oldIndex, index)
        }
    }

    /// スクロール時の処理
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val fullWidth = content.width
        val maxScrollOffset = fullWidth - frame.size.width
        if (scrollX < 0) {
            // 左端を超える場合は右端に移動する
            scrollX = maxScrollOffset.toInt()
        } else if (maxScrollOffset < scrollX) {
            // 右端を超える場合は左端に移動する
            scrollX -= maxScrollOffset.toInt()
        }
        updateButtonPosition()

    }

    /// 各ボタンのX座標を調整する
    private fun updateButtonPosition() {
        if (titles.isEmpty()) {
            return
        }
        // まずはページ順に横並び
        buttons.enumerated().forEach { it.element.frame.origin.x = buttonWidth * it.offset }
        // 右端のさらに右が見える場合、ボタンをさらに右にもってくる
        if (isScrollEnabled) {
            val marginCount = this.marginCount
            (0 .. marginCount).forEach { index  ->
                if (buttonWidth * (buttons.size - (marginCount - index)) < scrollX) {
                    // TODO ボタンの位置を調整する
                    buttons.safeGet(index)?.frame?.origin?.x = buttonWidth * (buttons.size + index)
                }
            }
            updateDummyButton()
        }
    }

    private fun updateDummyButton() {
        // ダミーボタンは常に右端に表示する
        val maxX = buttons.map({ it.frame.maxX }).sorted().lastOrNull()
        if (maxX != null) {
            dummyButton.frame.origin.x = maxX
        }
        val // 左端のボタンを取得してダミーボタンに反映
                targetButton = leftEndButton
        if (targetButton != null) {
            dummyButton.title = targetButton.title
            dummyButton.isSelected = targetButton.isSelected
        }
    }

    /// スクロールコンテンツのサイズを設定する
    private fun updateScrollSize() {
        if (isFitButtons) {
            // 横幅一杯とする
            content.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        } else {
            // ボタン数 + 余白1ページ分のサイズをとる
            content.layoutParams = FrameLayout.LayoutParams((scrollButtonWidth * titles.size).toInt(), MATCH_PARENT)
        }
    }

    /// ボタンの横幅を更新設定する
    private fun updateButtonSize() {
        if (titles.isEmpty()) {
            return
        }
        buttons.forEach {
            it.layoutParams = FrameLayout.LayoutParams(buttonWidth.toInt(), MATCH_PARENT)
        }
        dummyButton.layoutParams = FrameLayout.LayoutParams(buttonWidth.toInt(), MATCH_PARENT)
    }

    /// ボタンのタイトルを設定する
    private fun updateButtonTitles() {
        zip(buttons, titles).forEach {
            val button = it.first
            val title = it.second
            button.title = title
        }
    }

    /// ボタンの見た目を更新する
    private fun updateButton() {
        val updater = buttonUpdater ?: this::defaultButtonStateUpdater
        buttons.forEach {
            val buttonState = if (it.isSelected) ButtonState.selected else ButtonState.unselected
            updater(it, buttonState)
        }
    }

    // MARK: - Other
    /// ダミーボタンかどうか
    private fun isDummyButton(button: UIButton) : Boolean =
        button == dummyButton

    /// 選択中のボタンが中心に表示されるように位置を調整する
    private fun moveToCenter(animated: Boolean) {
        if (!isScrollEnabled) {
            scrollX = 0
            return
        }
        val index = selectedIndex ?: return
        val x1 = scrollButtonWidth * index - (frame.width / 2) + (scrollButtonWidth / 2)
        val x2 = x1 + (scrollButtonWidth * titles.size)
        if (abs(scrollX - x1) < abs(scrollX - x2) && animated) {
            setContentOffset(CGPoint(x = x1, y = 0.0), animated = animated)
        } else {
            setContentOffset(CGPoint(x = x2, y = 0.0), animated = animated)
        }
    }

    private fun setContentOffset(point: CGPoint, animated: Boolean) {
        if (animated) {
            smoothScrollTo(point.x.toInt(), point.y.toInt())
        } else {
            scrollTo(point.x.toInt(), point.y.toInt())
        }
    }

    public enum class ButtonState {
        unselected,
        selected
    }
}
