package kibaan.android.sample.screen.page

import android.view.View
import kibaan.android.framework.ScreenService
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.sample.R
import kibaan.android.ui.SmartLabel

class InstancePageViewController(private val text: String) : SmartViewController() {

    @IBOutlet(R.id.textLabel) lateinit var textLabel: SmartLabel

    // region -> Life cycle

    override fun onEnterForeground() {
        super.onEnterForeground()
        textLabel.text = this.text
    }

    // endregion

    // region -> Action

    @IBAction(R.id.backButton)
    fun actionRemoveNextScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        if (navigationRootController != null) {
            navigationRootController?.removeNextScreen()
        } else {
            ScreenService.shared.removeSubScreen()
        }
    }

    // endregion
}