package kibaan.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ScrollView
import kibaan.R
import kibaan.ios.CGSize
import kibaan.ios.IBInspectable
import kibaan.ios.didSet
import kibaan.ios.frame

class SmartScrollView : ScrollView {

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

    // region -> IBInspectable

    @IBInspectable var topIndicatorImage: BitmapDrawable? by didSet(null) {
        scrollIndicatorDrawer.topIndicatorBitmap = topIndicatorImage?.bitmap
    }

    @IBInspectable var bottomIndicatorImage: BitmapDrawable? by didSet(null) {
        scrollIndicatorDrawer.bottomIndicatorBitmap = bottomIndicatorImage?.bitmap
    }

    // endregion

    // region -> Variables

    private var scrollIndicatorDrawer: ScrollIndicatorDrawer = ScrollIndicatorDrawer(context)

    /**
     * スクロールが発生するかどうかを返します.
     * @return スクロール発生の有無
     */
    private val isScroll: Boolean get() = height < contentSize.height

    @Suppress("MemberVisibilityCanBePrivate")
    val contentSize: CGSize
        get() {
            return if (0 < childCount) getChildAt(0).frame.size else CGSize.zero
        }

    @Suppress("MemberVisibilityCanBePrivate")
    var indicatorSize: CGSize? = null

    // endregion

    // region -> Initialize

    fun setup(context: Context, attrs: AttributeSet? = null) {
        // プロパティの読み込み
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.SmartScrollView)
            topIndicatorImage = array.getDrawable(R.styleable.SmartScrollView_topIndicatorImage) as? BitmapDrawable
            bottomIndicatorImage = array.getDrawable(R.styleable.SmartScrollView_bottomIndicatorImage) as? BitmapDrawable
            array.recycle()
        }
    }

    // endregion

    // region -> Life cycle

    override fun onScrollChanged(x: Int, y: Int, oldx: Int, oldy: Int) {
        super.onScrollChanged(x, y, oldx, oldy)
        // スクロールが発生する場合
        if (isScroll) {
            if (y <= 0) {
                scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.BOTTOM)
            } else {
                if (y < contentSize.height - height) {
                    scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.BOTH)
                } else {
                    scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.TOP)
                }
            }
        } else {
            scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.NONE)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (isScroll) {
            if (scrollY <= 0) {
                scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.BOTTOM)
            } else {
                if (scrollY < contentSize.height - height) {
                    scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.BOTH)
                } else {
                    scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.TOP)
                }
            }
        } else {
            scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.NONE)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!isScroll) {
            scrollIndicatorDrawer.setScrollIndicatorType(ScrollIndicatorDrawer.ScrollIndicatorType.NONE)
        }
    }

    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        // フォーカスを指定またはクリアされた場合でも、スクロール位置を動かさないようにしています
        return true
    }

    @Suppress("NAME_SHADOWING")
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        val canvas = canvas ?: return
        scrollIndicatorDrawer.draw(this, canvas, scrollY, width)
    }

    // endregion
}
