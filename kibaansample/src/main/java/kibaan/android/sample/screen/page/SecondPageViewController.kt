package kibaan.android.sample.screen.page

import android.annotation.SuppressLint
import android.view.View
import kibaan.android.framework.BaseViewController
import kibaan.android.framework.ScreenService
import kibaan.android.framework.ViewControllerCache
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.sample.R
import kibaan.android.ui.SmartLabel

class SecondPageViewController: BaseViewController() {

    // region -> Outlets

    @IBOutlet(R.id.title_label) lateinit var titleLabel: SmartLabel

    // endregion

    // region -> Variables

    var count: Int = 0

    // endregion

    // region -> Life cycle

    @SuppressLint("SetTextI18n")
    override fun onEnterForeground() {
        super.onEnterForeground()
        titleLabel.text = "${javaClass.simpleName}_$count"
    }

    // endregion

    // region -> Action

    @IBAction(R.id.add_sub_screen_first_button)
    fun actionAddSubScreen(sender: View) {
        val nextCount = this.count + 1
        ScreenService.shared.addSubScreen(FirstPageViewController::class, id = "test_$nextCount", prepare = {
            it.count = nextCount
        })
    }

    @IBAction(R.id.add_sub_screen_second_button)
    fun actionAddSubScreenSecond(sender: View) {
        val nextCount = this.count + 1
        ScreenService.shared.addSubScreen(SecondPageViewController::class, id = "test_$nextCount", prepare = {
            it.count = nextCount
        })
    }

    @IBAction(R.id.remove_sub_screen_button)
    fun actionRemoveSubScreen(sender: View) {
        ScreenService.shared.removeSubScreen()
    }

    @IBAction(R.id.remove_sub_screen_target_button)
    fun actionRemoveSubScreenTarget(sender: View) {
        ScreenService.shared.removeSubScreen(to = ViewControllerCache.shared.get(FirstPageViewController::class, id = "test_0"))
    }

    @IBAction(R.id.remove_all_sub_screen_button)
    fun actionRemoveAllSubScreen(sender: View) {
        ScreenService.shared.removeAllSubScreen()
    }

    // endregion
}