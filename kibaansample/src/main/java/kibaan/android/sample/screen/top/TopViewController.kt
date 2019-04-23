package kibaan.android.sample.screen.top

import android.view.View
import kibaan.android.extension.integerValue
import kibaan.android.extension.stringValue
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBAction
import kibaan.android.ios.IBOutlet
import kibaan.android.sample.R
import kibaan.android.sample.screen.connection.ConnectionViewController
import kibaan.android.sample.screen.sub.ButtonViewController
import kibaan.android.sample.screen.sub.SubViewController
import kibaan.android.sample.screen.table.SampleTableViewController
import kibaan.android.framework.ScreenService
import kibaan.android.sample.model.AppSetting
import kibaan.android.sample.screen.page.FirstPageViewController
import kibaan.android.ui.SmartLabel
import kibaan.android.util.Log

/**
 * トップ画面
 */
class TopViewController : SmartViewController() {

    override fun viewDidLoad() {
        super.viewDidLoad()

    }

    @IBAction(R.id.page_button)
    fun actionPageButton(sender: View) {
        ScreenService.shared.addSubScreen(FirstPageViewController::class, id = "test_0")
    }

    @IBAction(R.id.count_up_button)
    fun actionCountUp(sender: View) {
    }

    @IBAction(R.id.connection_button)
    fun actionConnectionButton(sender: View) {
        Log.d(javaClass.simpleName, "actionConnectionButton: ${System.currentTimeMillis()}")
        ScreenService.shared.addSubScreen(ConnectionViewController::class)
    }

    @IBAction(R.id.table_button)
    fun actionTableButton(sender: View) {
        Log.d(javaClass.simpleName, "actionTableButton: ${System.currentTimeMillis()}")
        ScreenService.shared.addSubScreen(SampleTableViewController::class)
    }

    @IBAction(R.id.button_button)
    fun actionButtonButton(sender: View) {
        ScreenService.shared.addSubScreen(ButtonViewController::class)
    }

    @IBAction(R.id.sub_button)
    fun actionSubButton(sender: View) {
        ScreenService.shared.addSubScreen(SubViewController::class)
    }

}