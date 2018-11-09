package jp.co.altonotes.android.kibaan.sample.screen.top

import android.databinding.DataBindingUtil
import android.view.View
import jp.co.altonotes.android.kibaan.controller.BaseViewController
import jp.co.altonotes.android.kibaan.ios.IBAction
import jp.co.altonotes.android.kibaan.ios.IBOutlet
import jp.co.altonotes.android.kibaan.sample.R
import jp.co.altonotes.android.kibaan.sample.databinding.TopViewControllerBinding
import jp.co.altonotes.android.kibaan.sample.screen.connection.ConnectionViewController
import jp.co.altonotes.android.kibaan.sample.screen.sub.SubViewController
import jp.co.altonotes.android.kibaan.sample.screen.table.SampleTableViewController
import jp.co.altonotes.android.kibaan.service.ScreenService
import jp.co.altonotes.android.kibaan.ui.SmartButton
import jp.co.altonotes.android.kibaan.util.Log

/**
 * トップ画面
 */
class TopViewController : BaseViewController() {

    @IBOutlet(R.id.connection_button) lateinit var connectionButton: SmartButton
    @IBOutlet(R.id.table_button) lateinit var tableButton: SmartButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        val binding = DataBindingUtil.bind<TopViewControllerBinding>(view)
        binding?.controller = this

    }

    fun actionOrderButton(sender: View) {
        Log.i(javaClass.simpleName, "actionOrderButton")
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

    @IBAction(R.id.sub_button)
    fun actionSubButton(sender: View) {
        ScreenService.shared.addSubScreen(SubViewController::class)
    }

    // endregion
}