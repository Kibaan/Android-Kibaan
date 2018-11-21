package jp.co.altonotes.android.kibaan.sample.screen.top

import android.databinding.DataBindingUtil
import android.graphics.BitmapFactory
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import jp.co.altonotes.android.kibaan.controller.BaseViewController
import jp.co.altonotes.android.kibaan.ios.IBAction
import jp.co.altonotes.android.kibaan.ios.IBOutlet
import jp.co.altonotes.android.kibaan.ios.UIButton
import jp.co.altonotes.android.kibaan.ios.UIControlState
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
    @IBOutlet(R.id.set_image_test_button) lateinit var setImageTestButton: UIButton

    private var setImageCount = 0

    override fun viewDidLoad() {
        super.viewDidLoad()

        val binding = DataBindingUtil.bind<TopViewControllerBinding>(view)
        binding?.controller = this

        setImageTestButton.setImage(R.mipmap.checkbox_off, state = UIControlState.normal)
        setImageTestButton.setImage(R.mipmap.checkbox_on, state = UIControlState.selected)
        setImageTestButton.setImage(R.mipmap.radio_on, state = UIControlState.disabled)

    }

    @IBAction(R.id.set_image_state_change_button)
    fun actionSetImageTestButton(sender: View) {
        setImageCount++
        setImageTestButton.isSelected = false
        setImageTestButton.isEnabled = true
        when {
            setImageCount % 4 == 1 -> {
                setImageTestButton.isSelected = true
                setImageTestButton.title = "Selected"
            }
            setImageCount % 4 == 2 -> {
                setImageTestButton.isEnabled = false
                setImageTestButton.title = "Disabled"
            }
            setImageCount % 4 == 3 -> {
                setImageTestButton.isEnabled = false
                setImageTestButton.isSelected = true
                setImageTestButton.title = "Selected & Disabled"
            }
        }
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