package kibaan.android.ios

import android.content.Context
import android.graphics.Color
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
    private var normalButtonColor = UIColor(rgbValue = 0xDEDEDE)
//    var button = EllipseButton(context)

    //    private var selectedButtonColor = UIColor(rgbValue = 0x555555)
    private var selectedButtonColor = UIColor.red

    // endregion

    // region -> Variables

    var onPageChanged: ((UIPageControl) -> Unit)? = null

    // endregion

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setupPageControl(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupPageControl(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupPageControl(context, attrs)
    }

    private fun setupPageControl(context: Context, attrs: AttributeSet? = null) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.UIPageControl)
            numberOfPages = array.getInt(R.styleable.UIPageControl_numberOfPages, numberOfPages)
            currentPage = array.getInt(R.styleable.UIPageControl_currentPage, currentPage)
            pageIndicatorTintColor = UIColor(array.getColor(R.styleable.UIPageControl_pageIndicatorTintColor, normalButtonColor.intValue))
            currentPageIndicatorTintColor = UIColor(array.getColor(R.styleable.UIPageControl_currentPageIndicatorTintColor, selectedButtonColor.intValue))
            array.recycle()
        }
    }
    // endregion

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
    }

    /*numberOfPagesを呼ばないとbuttonを生成することができない*/
    var numberOfPages: Int by didSet(0) {
        constructButtons()
    }

    /*現在のページ*/
    var currentPage: Int by didSet(0) {
        (0 until childCount).forEach { index ->
            getChildAt(index).isSelected = currentPage == index
        }
    }

    /*選択されていないページの色を設定する*/
    var pageIndicatorTintColor: UIColor by didSet(normalButtonColor) {
        (0 until childCount).mapNotNull { getChildAt(it) as? SmartButton }.forEach {
            it.setBackgroundColor(pageIndicatorTintColor, UIControlState.normal)
        }
    }

    /*選択されているページの色を設定する*/
    var currentPageIndicatorTintColor: UIColor by didSet(UIColor.clear) {
        (0 until childCount).mapNotNull { getChildAt(it) as? SmartButton }.forEach {
            it.setBackgroundColor(currentPageIndicatorTintColor, UIControlState.selected)
        }
    }

    /*pageControlButtonを生成・設定する*/
    private fun constructButtons() {
        removeAllViews()
        (0 until numberOfPages).forEach { offset ->
            val button = EllipseButton(context)
            val layoutParams = LinearLayout.LayoutParams(buttonSize, buttonSize)
            val leftMargin = if (offset != 0) this.leftMargin else 0
            layoutParams.setMargins(leftMargin, 0, 0, 0)
            button.layoutParams = layoutParams
            if (button.backgroundColor(forState = UIControlState.normal) == null) {
                button.setBackgroundColor(normalButtonColor, forState = UIControlState.normal)
            }
            button.setBackgroundColor(selectedButtonColor, forState = UIControlState.selected)
            button.setOnClickListener {
                currentPage = offset
                onPageChanged?.invoke(this)
            }
            addView(button)
        }
    }
}