package jp.co.altonotes.android.kibaan.sample.screen.sub

import android.os.Handler
import android.view.View
import jp.co.altonotes.android.kibaan.controller.BaseViewController
import jp.co.altonotes.android.kibaan.service.ScreenService
import jp.co.altonotes.android.kibaan.extension.stringValue
import jp.co.altonotes.android.kibaan.ios.IBAction
import jp.co.altonotes.android.kibaan.ios.IBOutlet
import jp.co.altonotes.android.kibaan.ios.isGone
import jp.co.altonotes.android.kibaan.sample.R
import jp.co.altonotes.android.kibaan.sample.screen.table.SampleTableViewController
import jp.co.altonotes.android.kibaan.sample.screen.top.TopViewController
import jp.co.altonotes.android.kibaan.ui.SmartButton
import jp.co.altonotes.android.kibaan.ui.SmartTextView

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