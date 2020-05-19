package kibaan.android.sample.controller

import android.view.View
import android.widget.FrameLayout
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBOutlet
import kibaan.android.ios.StringEnumDefault
import kibaan.android.sample.R

class MockViewController : MockBaseViewController() {

    // region -> Outlets

    @IBOutlet(R.id.container_view) lateinit var containerView: FrameLayout

    // endregion

    // region -> Variables

    val subVc1 = MockSubViewController()
    val subVc2 = MockSubViewController()
    var tab: Tab = Tab.tab1

    override val nextScreenContainer: View get() = containerView

    override val foregroundSubControllers: List<SmartViewController>
        get() = if (tab == Tab.tab1) listOf(subVc1) else listOf(subVc2)

    // endregion

    // region -> Function

    fun changeTab(tab: Tab) {
        leaveForegroundSubControllers()
        this.tab = tab
        enterForegroundSubControllers()
    }

    // endregion

    // region -> Enum

    enum class Tab : StringEnumDefault {
        tab1, tab2
    }

    // endregion
}
