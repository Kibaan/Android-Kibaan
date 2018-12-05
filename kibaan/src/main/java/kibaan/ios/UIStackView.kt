package kibaan.ios

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kibaan.ui.RoundedLinearLayout

/**
 * Created by yamamoto on 2018/01/25.
 */
open class UIStackView : RoundedLinearLayout {

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion

    val arrangedSubviews: List<View> get() = (0 until childCount).map { getChildAt(it) }

    var axis: Int
        get() = orientation
        set(value) {
            orientation = value
        }

    fun addArrangedSubview(childView: View, layout: ViewGroup.LayoutParams? = null) {
        if (layout != null) {
            addView(childView, layoutParams)
        } else {
            addView(childView)
        }
    }

    fun removeArrangedSubview(childView: View) {
        removeView(childView)
    }
}