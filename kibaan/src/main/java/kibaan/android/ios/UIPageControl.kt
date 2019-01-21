package kibaan.android.ios

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import kibaan.android.R
import kibaan.android.ui.EllipseButton
import kibaan.android.ui.SmartButton
import kibaan.android.util.DeviceUtils

class UIPageControl : LinearLayout {

    // region -> Constants

    private var leftMargin = DeviceUtils.toDp(context, 60)
    private var buttonSize = DeviceUtils.toDp(context, 50)
    private var normalDefaultColor = UIColor(rgbValue = 0xDEDEDE)
    private var selectedDefaultColor = UIColor(rgbValue = 0x555555)

    // endregion

    // region -> Variables

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    var onPageChanged: ((UIPageControl) -> Unit)? = null

    /* numberOfPagesを呼ばないとbuttonを生成することができない */
    /* PageControlのページ数を設定する */
    var numberOfPages: Int by didSet(0) {
        constructButtons()
    }

    /* 現在のページ */
    var currentPage: Int by didSet(0) {
        (0 until childCount).forEach { index ->
            getChildAt(index).isSelected = currentPage == index
        }
    }

    /* 選択されていないページの色を設定する */
    var pageIndicatorTintColor: UIColor by didSet(normalDefaultColor) {
        (0 until childCount).mapNotNull { getChildAt(it) as? SmartButton }.forEach {
            it.setBackgroundColor(pageIndicatorTintColor, UIControlState.normal)
        }
    }

    /* 選択されているページの色を設定する */
    var currentPageIndicatorTintColor: UIColor by didSet(UIColor.clear) {
        (0 until childCount).mapNotNull { getChildAt(it) as? SmartButton }.forEach {
            it.setBackgroundColor(currentPageIndicatorTintColor, UIControlState.selected)
        }
    }

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        readAttributes(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        readAttributes(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readAttributes(context, attrs)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet? = null) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UIPageControl)
            numberOfPages = array.getInt(R.styleable.UIPageControl_numberOfPages, numberOfPages)
            currentPage = array.getInt(R.styleable.UIPageControl_currentPage, currentPage)
            pageIndicatorTintColor = UIColor(
                array.getColor(
                    R.styleable.UIPageControl_pageIndicatorTintColor,
                    normalDefaultColor.intValue
                )
            )
            currentPageIndicatorTintColor = UIColor(
                array.getColor(
                    R.styleable.UIPageControl_currentPageIndicatorTintColor,
                    selectedDefaultColor.intValue
                )
            )
            array.recycle()
        }
    }
    // endregion

    /* pageControlButtonを生成・設定する */
    private fun constructButtons() {
        removeAllViews()
        (0 until numberOfPages).forEach { offset ->
            val button = EllipseButton(context)
            val layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
            val leftMargin = if (offset != 0) this.leftMargin else 0
            layoutParams.setMargins(leftMargin, 0, 0, 0)
            button.layoutParams = layoutParams
            if (button.backgroundColor(forState = UIControlState.normal) == null) {
                button.setBackgroundColor(normalDefaultColor, forState = UIControlState.normal)
            }
            button.setBackgroundColor(selectedDefaultColor, forState = UIControlState.selected)
            button.setOnClickListener {
                currentPage = offset
                onPageChanged?.invoke(this)
            }
            addView(button)
        }
    }
}