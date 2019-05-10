package kibaan.android.ui

import android.widget.HorizontalScrollView
import kibaan.android.extension.height
import kibaan.android.extension.width
import kibaan.android.ios.*
import kotlin.math.abs


/// 無限スクロールするセグメントボタン群
/// - 全ボタンが横幅に収まりきる場合は、隙間が空かないよう全ボタンを均等配置して、スクロールを無効にする
/// - ループを考慮して1つダミーのボタンを作る（左右端どちらにも同じボタンが見える場合があるため）
class ScrollSegmentedButton: HorizontalScrollView {

    @IBInspectable
    var textSize: CGFloat = 14.0
    @IBInspectable var scrollButtonWidth: CGFloat = 100.0
    @IBInspectable var titlesText: String
        get() {
            return titles.joined(separator = ",")
        }
        set(value) {
            titles = value.components(separatedBy = ",")
        }
    @IBInspectable var buttonCount: Int = 0
        set(newValue) {
            val oldValue = field
            field = newValue
            setup(buttonCount = buttonCount)
        }
    /// 実際に表示するボタンの横幅
    val buttonWidth: CGFloat
        get() = if (isFitButtons) (frame.width / CGFloat(titles.size)) else scrollButtonWidth
    /// 選択中のインデックス
    var selectedIndex: Int?
        get() {
            if (titles.size <= buttons.size) {
                return buttons[0 until titles.size].enumerated().firstOrNull()(where = { it.element.isSelected })?.offset
            }
            return 0
        }
        set(value) {
            select(value, needsCallback = true)
        }
    var titles: List<String> = listOf()
        set(newValue) {
            val oldValue = field
            field = newValue
            updateButtonTitles()
        }
    var buttons: List<UIButton> = listOf()
    var onSelected: ((oldIndex: Int?, index: Int) -> Unit)? = null
    private// MARK: - Private Variables
    /// ダミーボタン
    var dummyButton: UIButton = UIButton(context)
    private/// 直前のサイズ
    /// サイズが変わった場合にページサイズや位置の調整が必要なため、サイズ変更が分かるよう直前のサイズを保存している
    var previousSize: CGSize? = null
    private/// 余白部分に移動するボタン個数
    val marginCount: Int
        get() = Int(frame.width / scrollButtonWidth)
    private/// ボタンが端末の横幅に収まるか
    val isFitButtons: Boolean
        get() = CGFloat(titles.size) * scrollButtonWidth <= frame.width
    private/// 左端に表示しているボタン
    val leftEndButton: UIButton?
        get() = buttons.sorted(by = { lhs, rhs  ->
            lhs.frame.minX < rhs.frame.minX
        }).firstOrNull()
    private/// ボタンの見た目を更新する為の処理
    var buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null

    // MARK: - Life cycle
    override public constructor(frame: CGRect) : super(frame = frame) {    commonInit()
    }

    public constructor(aDecoder: NSCoder) : super(coder = aDecoder) {    commonInit()
    }

    fun commonInit() {
        delegate = this
        showsHorizontalScrollIndicator = false
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
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

    // MARK: - Initializer
    fun setup(buttonCount: Int, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.buttonUpdater = buttonUpdater
        makeButtons(buttonCount = buttonCount, buttonMaker = buttonMaker ?: makeDefaultButton)
    }

    fun setup(titles: List<String>, buttonMaker: (() -> UIButton)? = null, buttonUpdater: ((UIButton, ButtonState) -> Unit)? = null) {
        this.titles = titles
        this.buttonUpdater = buttonUpdater
        makeButtons(buttonCount = titles.size, buttonMaker = buttonMaker ?: makeDefaultButton)
    }

    fun makeDefaultButton() : UIButton {
        val button = SmartButton(frame = .zero)
        button.titleFont = UIFont.systemFont(ofSize = textSize)
        button.setTitleColor(.lightGray, for = .normal)
        button.setTitleColor(.white, for = .selected)
        button.adjustsFontSizeToFitWidth = true
        button.miniumScaleFactor = 0.5
        return button
    }

    fun defaultButtonStateUpdater(button: UIButton, buttonState: ButtonState) {
        val pointSize = button.titleLabel?.font?.pointSize ?: return
        when (buttonState) {
                .ButtonState.unselected
            -> button.titleLabel?.font = UIFont.systemFont(ofSize = pointSize)
            .selected -> button.titleLabel?.font = UIFont.boldSystemFont(ofSize = pointSize)
        }
    }

    fun makeButtons(buttonCount: Int, buttonMaker: (() -> UIButton)) {
        clearView()
        buttons = (0 until buttonCount).map { _  ->
            val button = buttonMaker()
            addSubview(button)
            button.addTarget(this, action = #selector(self.actionSelect(_:)), for = .touchUpInside)
            button
        }
        dummyButton = buttonMaker()
        addSubview(dummyButton)
        updateButtonTitles()
        updateButtonPosition()
        isScrollEnabled = !isFitButtons
    }

    // MARK: - Others
    fun select(index: Int?, animated: Boolean = true, needsCallback: Boolean = true) {
        val index = index ?: return
        buttons.forEach { it.isSelected = false }
        buttons[safe: index]?.isSelected = true
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
        subviews.forEach { it.removeFromSuperview() }
    }

    // MARK: - Action
    private fun actionSelect(button: SmartButton) {
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

    // MARK: - UIScrollViewDelegate
    public/// スクロール時の処理
    fun scrollViewDidScroll(scrollView: UIScrollView) {
        val fullWidth = contentSize.width
        val maxScrollOffset = fullWidth - frame.size.width
        if (contentOffset.x < 0) {
            // 左端を超える場合は右端に移動する
            contentOffset.x = maxScrollOffset
        } else if (maxScrollOffset < contentOffset.x) {
            // 右端を超える場合は左端に移動する
            contentOffset.x -= maxScrollOffset
        }
        updateButtonPosition()
    }

    // MARK: - Update
    private/// 各ボタンのX座標を調整する
    fun updateButtonPosition() {
        if (titles.isEmpty()) {
            return
        }
        // まずはページ順に横並び
        buttons.enumerated().forEach { it.element.frame.origin.x = buttonWidth * CGFloat(it.offset) }
        // 右端のさらに右が見える場合、ボタンをさらに右にもってくる
        if (isScrollEnabled) {
            val marginCount = this.marginCount(0 .. marginCount).forEach { index  ->
                if (buttonWidth * CGFloat(buttons.size - (marginCount - index)) < contentOffset.x) {
                    buttons[safe: index]?.frame?.origin?.x = buttonWidth * CGFloat(buttons.size + index)
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
            dummyButton.setTitle(targetButton.title(for = .normal), for = .normal)
            dummyButton.isSelected = targetButton.isSelected
        }
    }

    /// スクロールコンテンツのサイズを設定する
    private fun updateScrollSize() {
        if (isFitButtons) {
            // 横幅一杯とする
            contentSize = CGSize(width = frame.width, height = frame.height)
        } else {
            // ボタン数 + 余白1ページ分のサイズをとる
            contentSize = CGSize(width = scrollButtonWidth * CGFloat(titles.size) + frame.width, height = frame.height)
        }
    }

    /// ボタンの横幅を更新設定する
    private fun updateButtonSize() {
        if (titles.isEmpty()) {
            return
        }
        buttons.forEach {
            it.frame.size.width = buttonWidth
            it.frame.size.height = frame.height
        }
        dummyButton.frame.size.width = buttonWidth
        dummyButton.frame.size.height = frame.height
    }

    /// ボタンのタイトルを設定する
    private fun updateButtonTitles() {
        zip(buttons, titles).forEach { button, title  ->
            button.setTitle(title, for = .normal)
        }
    }

    /// ボタンの見た目を更新する
    private fun updateButton() {
        val updater = buttonUpdater ?: defaultButtonStateUpdater
        buttons.forEach {
            val buttonState = if (it.isSelected) ButtonState.selected else ButtonState.unselected
            updater(it, buttonState)
        }
    }

    // MARK: - Other
    /// ダミーボタンかどうか
    private fun isDummyButton(button: SmartButton) : Boolean =
        button == dummyButton

    /// 選択中のボタンが中心に表示されるように位置を調整する
    private fun moveToCenter(animated: Boolean) {
        if (!isScrollEnabled || contentSize == .zero) {
            contentOffset.x = 0
            return
        }
        val index = selectedIndex ?: return
        val x1 = scrollButtonWidth * CGFloat(index) - (frame.width / 2) + (scrollButtonWidth / 2)
        val x2 = x1 + (scrollButtonWidth * CGFloat(titles.size))
        if (abs(contentOffset.x - x1) < abs(contentOffset.x - x2) && animated) {
            setContentOffset(CGPoint(x = x1, y = 0), animated = animated)
        } else {
            setContentOffset(CGPoint(x = x2, y = 0), animated = animated)
        }
    }
    public enum class ButtonState {
        unselected,
        selected
    }
}
