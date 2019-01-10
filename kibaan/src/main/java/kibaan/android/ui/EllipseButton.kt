package kibaan.android.ui

import android.content.Context
import android.util.AttributeSet

class EllipseButton : SmartButton {

    // region -> Constructor

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    // endregion

    // region -> Initializer

    private fun setup() {
        isEllipse = true
    }
}