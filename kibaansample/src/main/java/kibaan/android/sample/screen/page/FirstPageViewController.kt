package kibaan.android.sample.screen.page

import android.annotation.SuppressLint
import android.view.View
import kibaan.android.framework.SmartViewController
import kibaan.android.framework.ScreenService
import kibaan.android.framework.ViewControllerCache
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.sample.R
import kibaan.android.ui.SmartLabel

class FirstPageViewController : SmartViewController() {

    // region -> Outlets

    @IBOutlet(R.id.contentsView) lateinit var contentsView: View
    @IBOutlet(R.id.title_label) lateinit var titleLabel: SmartLabel

    // endregion

    // region -> Variables

    var count: Int = 0

    override val nextScreenContainer: View get() = contentsView

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
    fun actionAddSubScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        val nextCount = this.count + 1
        ScreenService.shared.addSubScreen(FirstPageViewController::class, id = "test_$nextCount", prepare = {
            it.count = nextCount
        })
    }

    @IBAction(R.id.add_sub_screen_second_button)
    fun actionAddSubScreenSecond(@Suppress("UNUSED_PARAMETER") sender: View) {
        val nextCount = this.count + 1
        ScreenService.shared.addSubScreen(SecondPageViewController::class, id = "test_$nextCount", prepare = {
            it.count = nextCount
        })
    }

    @IBAction(R.id.add_sub_screen_view_controller_button)
    fun actionAddSubScreenViewController(@Suppress("UNUSED_PARAMETER") sender: View) {
        val controller = InstancePageViewController(text = "インスタンス生成")
        ScreenService.shared.addSubScreenViewController(controller)
    }

    @IBAction(R.id.remove_sub_screen_button)
    fun actionRemoveSubScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        ScreenService.shared.removeSubScreen()
    }

    @IBAction(R.id.remove_sub_screen_target_button)
    fun actionRemoveSubScreenTarget(@Suppress("UNUSED_PARAMETER") sender: View) {
        ScreenService.shared.removeSubScreen(
            to = ViewControllerCache.shared.get(
                SecondPageViewController::class,
                id = "test_2"
            )
        )
    }

    @IBAction(R.id.remove_all_sub_screen_button)
    fun actionRemoveAllSubScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        ScreenService.shared.removeAllSubScreen()
    }

    @IBAction(R.id.add_next_screen)
    fun actionAddNextScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        val targetViewController = navigationRootController ?: this
        val nextCount = this.count + 1
        targetViewController.addNextScreen(SecondPageViewController::class, id = "test_$nextCount", prepare = {
            it.count = nextCount
        })
    }

    @IBAction(R.id.add_next_screen_view_controller)
    fun actionAddNextScreenViewController(@Suppress("UNUSED_PARAMETER") sender: View) {
        val targetViewController = navigationRootController ?: this
        val controller = InstancePageViewController(text = "インスタンス生成したけど、表示されてる？")
        targetViewController.addNextScreenViewController(controller)
    }


    @IBAction(R.id.remove_next_screen)
    fun actionRemoveNextScreen(@Suppress("UNUSED_PARAMETER") sender: View) {
        navigationRootController?.removeNextScreen()
    }

    // endregion
}