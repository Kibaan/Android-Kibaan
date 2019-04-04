package kibaan.android.sample.controller

import kibaan.android.framework.BaseViewController

open class MockBaseViewController : BaseViewController() {

    // region -> Variables

    var startCount = 0
    var stopCount = 0

    // endregion

    // region -> Life cycle

    override fun onEnterForeground() {
        startCount += 1
        super.onEnterForeground()
    }

    override fun onLeaveForeground() {
        stopCount += 1
        super.onLeaveForeground()
    }

    // endregion
}