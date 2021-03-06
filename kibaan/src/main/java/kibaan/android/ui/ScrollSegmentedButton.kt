package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.RelativeLayout
import kibaan.android.AndroidUnique
import kibaan.android.extension.getStringOrNull
import kibaan.android.ios.*
import kotlin.math.abs
import kotlin.math.max

/// 無限スクロールするセグメントボタン群
/// - 全ボタンが横幅に収まりきる場合は、隙間が空かないよう全ボタンを均等配置して、スクロールを無効にする
/// - ループを考慮して1つダミーのボタンを作る（左右端どちらにも同じボタンが見える場合があるため）
class ScrollSegmentedButton : HorizontalScrollView {

    @IBInspectable var textSize: CGFloat = 14.0
    var scrollButtonWidthPx: CGFloat = 300.0

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
            makeButtons(buttonCount = buttonCount, buttonMaker = buttonMaker ?: this::makeDefaultButton)
            updateScrollSize()
        }

    /// 実際に表示するボタンの横幅
    val buttonWidthPx: CGFloat
        get() {
            val count = max(buttonCount, 1)
            return if (isFitButtons) (width.toDouble() / count) else scrollButtonWidthPx
        }

    /// 選択中のインデックス
    var selectedIndex: Int?
        get() {
            return buttons.enumerated().firstOrNull { it.element.isSelected }?.offset
        }
        set(value) {
            select(value, needsCallback = true)
        }
    var titles: List<String> = listOf()
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
        get() = (width / scrollButtonWidthPx).toInt()

    /// ボタンが端末の横幅に収まるか
    private val isFitButtons: Boolean
        get() = buttonCount * scrollButtonWidthPx <= width

    /// 左端に表示しているボタン
    private val leftEndButton: UIButton?
        get() = buttons.sorted(by = { lhs, rhs ->
            when {
                lhs.frame.minX < rhs.frame.minX -> ComparisonResult.orderedAscending
                lhs.frame.minX > rhs.frame.minX -> ComparisonResult.orderedDescending
                else -> ComparisonResult.orderedSame
            }
        }).firstOrNull()

    private var buttonMaker: (() -> UIButton)? = null

    /// ボタンの見た目を更新する為の処理
    private var buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null

    @AndroidUnique
    private val scrollMargin = 5000

    @AndroidUnique
    private val animatedScrollGap: Long = 250

    @AndroidUnique
    private var smoothScrollingPoint: CGPoint? = null

    @AndroidUnique
    private val clearSmoothScrollingPoint = {
        smoothScrollingPoint = null
    }

    @AndroidUnique
    var isScrollEnabled = true

    @AndroidUnique
    val content = FrameLayout(context)
    // FrameLayoutの幅を伸ばすための支えのView
    private val horizontalSupportView = View(context)

    private val layoutListener: ViewTreeObserver.OnGlobalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            // サイズが変わった場合
            if (previousSize == null || previousSize != frame.size) {
                isScrollEnabled = !isFitButtons
                updateScrollSize()
                updateButtonSize()
                moveToCenter(animated = false)
                updateButtonPosition()
                previousSize = frame.size
            }

            viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }
    }

    init {
        isHorizontalScrollBarEnabled = false
    }

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
        horizontalSupportView.layoutParams = LayoutParams(width, LayoutParams.MATCH_PARENT)
        content.addView(horizontalSupportView)
        addView(content, RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, MATCH_PARENT))

        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, kibaan.android.R.styleable.ScrollSegmentedButton)

            // TODO サイズをpixelで保持してよいのか？
            textSize = array.getDimensionPixelSize(kibaan.android.R.styleable.ScrollSegmentedButton_android_textSize, textSize.toInt()).toDouble()
            scrollButtonWidthPx = array.getDimensionPixelSize(kibaan.android.R.styleable.ScrollSegmentedButton_scrollButtonWidth, scrollButtonWidthPx.toInt()).toDouble()

            titlesText = array.getStringOrNull(kibaan.android.R.styleable.ScrollSegmentedButton_titlesText) ?: titlesText
            buttonCount = array.getInt(kibaan.android.R.styleable.ScrollSegmentedButton_buttonCount, 0)

            array.recycle()
        }
    }

    // MARK: - Initializer
    fun setup(buttonCount: Int, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.buttonMaker = buttonMaker
        this.buttonUpdater = buttonUpdater
        this.buttonCount = buttonCount
    }

    fun setup(titles: List<String>, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.buttonMaker = buttonMaker
        this.buttonUpdater = buttonUpdater
        this.buttonCount = titles.count
        this.titles = titles.toMutableList()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 横画面状態から縦画面状態に戻った際に選択状態が中央に移動しない問題への対策
        viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
    }

    override fun canScrollHorizontally(direction: Int): Boolean {
        return isScrollEnabled
    }

    fun makeDefaultButton(): UIButton {
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
        dummyButton = buttonMaker()
        dummyButton.layoutParams = LayoutParams(buttonWidthPx.toInt(), LayoutParams.MATCH_PARENT)
        buttons = (0 until buttonCount).map { buttonMaker() }
        val allButtons = buttons.toMutableList()
        allButtons.append(dummyButton)
        allButtons.forEach { button ->
            button.layoutParams = LayoutParams(buttonWidthPx.toInt(), LayoutParams.MATCH_PARENT)
            content.addView(button)
            button.setOnClickListener {
                actionSelect(button)
            }
        }
        updateButtonTitles()
        updateButtonPosition()
        isScrollEnabled = !isFitButtons

        // ボタンが生成される際にリスナーを登録する（イニシャライザのみだと中身が変更される場合をカバー出来ない）
        viewTreeObserver.removeOnGlobalLayoutListener(layoutListener)
        viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
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
        titles = listOf()
        clearView()
    }

    fun clearView() {
        previousSize = null
        buttons.forEach {
            it.removeFromSuperview()
        }
        dummyButton.removeFromSuperview()
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
        val contentWidth = ((scrollButtonWidthPx * buttonCount)).toInt()
        val maxScrollOffset = scrollMargin + contentWidth
        if (scrollX < scrollMargin) {
            // 左端を超える場合は右端に移動する
            val moveDistance = contentWidth - (scrollMargin - scrollX)
            if (scrollX + moveDistance < scrollMargin) {
                scrollX = scrollMargin
            } else {
                scrollX += moveDistance
            }
        } else if (maxScrollOffset < scrollX) {
            // 右端を超える場合は左端に移動する
            scrollX -= contentWidth - (scrollX - maxScrollOffset)
        }
        updateButtonPosition()
    }

    /// 各ボタンのX座標を調整する
    private fun updateButtonPosition() {
        val margin = if (isFitButtons) 0 else scrollMargin

        // まずはページ順に横並び
        buttons.enumerated().forEach {
            val button = it.element
            val layoutParams = LayoutParams(button.layoutParams.width, button.layoutParams.height)
            layoutParams.leftMargin = (buttonWidthPx * it.offset).toInt() + margin
            button.layoutParams = layoutParams
        }

        if (!isScrollEnabled) return

        // 右端のさらに右が見える場合、ボタンをさらに右にもってくる
        val marginCount = this.marginCount
        (0..marginCount).forEach { index ->
            if (margin + buttonWidthPx * (index + 1) < scrollX) {
                val button = buttons.safeGet(index)
                if (button != null) {
                    val layoutParams = LayoutParams(button.layoutParams.width, button.layoutParams.height)
                    layoutParams.leftMargin = (buttonWidthPx * (buttons.size + index)).toInt() + margin
                    button.layoutParams = layoutParams
                }
            }
        }
        updateDummyButton()
    }

    private fun updateDummyButton() {
        // ダミーボタンは常に右端に表示する
        val maxX = buttons.map { ((it.layoutParams as? MarginLayoutParams)?.leftMargin ?: 0) + buttonWidthPx }.sorted().lastOrNull()
        if (maxX != null) {
            val layoutParams = LayoutParams(dummyButton.layoutParams.width, dummyButton.layoutParams.height)
            layoutParams.leftMargin = maxX.toInt()
            dummyButton.layoutParams = layoutParams
        }
        // 左端のボタンを取得してダミーボタンに反映
        val targetButton = leftEndButton
        if (targetButton != null) {
            dummyButton.title = targetButton.title
            dummyButton.isSelected = targetButton.isSelected
        }
    }

    /// スクロールコンテンツのサイズを設定する
    private fun updateScrollSize() {
        if (isFitButtons) {
            // 横幅一杯とする
            horizontalSupportView.layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)
        } else {
            // ボタン数 + 余白1ページ分のサイズをとる
            val width = (scrollButtonWidthPx * buttonCount) + (scrollMargin * 2)
            horizontalSupportView.layoutParams = LayoutParams(width.toInt(), MATCH_PARENT)
        }
    }

    /// ボタンの横幅を更新設定する
    private fun updateButtonSize() {
        buttons.forEach {
            it.layoutParams = LayoutParams(buttonWidthPx.toInt(), MATCH_PARENT)
        }
        dummyButton.layoutParams = LayoutParams(buttonWidthPx.toInt(), MATCH_PARENT)
    }

    /// ボタンのタイトルを設定する
    private fun updateButtonTitles() {
        zip(buttons, titles).forEach {
            val button = it.first
            val title = it.second
            button.title = title
        }
        updateDummyButton()
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
    private fun isDummyButton(button: UIButton): Boolean =
        button == dummyButton

    /// 選択中のボタンが中心に表示されるように位置を調整する
    private fun moveToCenter(animated: Boolean) {
        if (!isScrollEnabled) {
            scrollX = 0
            return
        }
        val scrollX = scrollX - scrollMargin
        val index = selectedIndex ?: return
        val x1 = scrollButtonWidthPx * index - (width / 2) + (scrollButtonWidthPx / 2)
        val x2 = x1 + (scrollButtonWidthPx * buttonCount)
        if (abs(scrollX - x1) < abs(scrollX - x2) && animated) {
            if (x1 < 0) {
                val contentWidth = ((scrollButtonWidthPx * buttonCount)).toInt()
                val maxScrollOffset = scrollMargin + contentWidth
                this.scrollX = maxScrollOffset
                setContentOffset(CGPoint(x = x2, y = 0.0), animated = animated)
            } else {
                setContentOffset(CGPoint(x = x1, y = 0.0), animated = animated)
            }
        } else {
            val contentWidth = ((scrollButtonWidthPx * buttonCount)).toInt()
            if (contentWidth < x2) {
                this.scrollX = scrollMargin
                setContentOffset(CGPoint(x = x1, y = 0.0), animated = animated)
            } else {
                setContentOffset(CGPoint(x = x2, y = 0.0), animated = animated)
            }
        }
    }

    private fun setContentOffset(point: CGPoint, animated: Boolean) {
        val x = point.x + scrollMargin
        if (animated) {
            if (smoothScrollingPoint?.x == point.x) {
                return
            }
            smoothScrollingPoint = point

            handler?.removeCallbacks(clearSmoothScrollingPoint)
            handler?.postDelayed(clearSmoothScrollingPoint, animatedScrollGap)

            if (handler == null) {
                smoothScrollingPoint = null
            }

            smoothScrollTo(x.toInt(), point.y.toInt())
        } else {
            scrollTo(x.toInt(), point.y.toInt())
        }
    }

    @AndroidUnique
    override fun fling(velocityX: Int) {
        // フリック操作した際の勢い(速度)を減少させている
        super.fling((velocityX * 0.5).toInt())
    }

    public enum class ButtonState {
        unselected,
        selected
    }
}
