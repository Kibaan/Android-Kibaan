package kibaan.android.sample.screen.sub

import android.content.Context
import android.util.AttributeSet
import kibaan.android.ui.CustomNibView

class SampleCustomView : CustomNibView {

    // region -> Constructor

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // endregion
}