package kibaan.sample.screen.sub

import android.os.Handler
import android.view.View
import kibaan.controller.BaseViewController
import kibaan.service.ScreenService
import kibaan.extension.stringValue
import kibaan.ios.IBAction
import kibaan.ios.IBOutlet
import kibaan.ios.isGone
import kibaan.sample.R
import kibaan.sample.screen.table.SampleTableViewController
import kibaan.sample.screen.top.TopViewController
import kibaan.ui.SmartButton
import kibaan.ui.SmartTextView

/**
 * サブ画面
 */
class SubViewController : BaseViewController() {

    val handler = Handler()

    var count = 0
    var indicatorCount = 0

    @IBOutlet(R.id.count_text) lateinit var countText: SmartTextView

    @IBOutlet(R.id.indicator_count_text) lateinit var indicatorCountText: SmartTextView

    @IBOutlet(R.id.sub_view_button1) lateinit var subViewButton1: SmartButton
    @IBOutlet(R.id.sub_view_button2) lateinit var subViewButton2: SmartButton

    override fun viewDidLoad() {
        super.viewDidLoad()
    }

    override fun onEnterForeground() {
        super.onEnterForeground()
        subViewButton2.isEnabled = true
    }

    /**
     * カウントアップ
     */
    @IBAction(R.id.count_button)
    fun actionCountButton(sender: View) {
        count += 1
        countText.text = count.stringValue
    }

    /**
     * インジケーター
     */
    @IBAction(R.id.indicator_button)
    fun actionIndicatorButton(sender: View) {
        indicatorCount += 1
        indicatorCountText.text = indicatorCount.stringValue

        val indicator = ScreenService.shared.showScreenIndicator()

        handler.postDelayed({
            complete(indicator)
        }, 1000)
    }

    @IBAction(R.id.sub_view_button1)
    fun actionSubViewButton1(sender: View) {
        ScreenService.shared.addSubScreen(SampleTableViewController::class)
        subViewButton2.isEnabled = false
    }

    @IBAction(R.id.sub_view_button2)
    fun actionSubViewButton2(sender: View) {
        ScreenService.shared.addSubScreen(TopViewController::class)
    }

    fun complete(indicator: View?) {

        handler.post {
            indicator?.isGone = true
        }

        handler.post {
            ScreenService.shared.addSubScreen(SampleTableViewController::class)
        }
    }

    /**
     * 閉じる
     */
    @IBAction(R.id.close_button)
    fun actionCloseButton(sender: View) {
        ScreenService.shared.removeSubScreen()
    }
}