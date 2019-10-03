package kibaan.android.ios

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar

open class UIActivityIndicatorView : ProgressBar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    val hidesWhenStopped: Boolean = true

    open fun startAnimating() {
        visibility = View.VISIBLE
    }

    open fun stopAnimating() {
        visibility = View.GONE
    }
}