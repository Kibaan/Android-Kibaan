package kibaan.android.sample.screen.other

import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBOutlet
import kibaan.android.sample.R
import kibaan.android.ui.ScrollSegmentedButton
import kibaan.android.ui.SmartButton
import kibaan.android.util.Log

/**
 * セグメント関連のUIチェック用の画面
 */
class SegmentCheckViewController : SmartViewController() {

    @IBOutlet(R.id.scrollSegment)
    lateinit var scrollSegment: ScrollSegmentedButton

    override fun viewDidLoad() {
        super.viewDidLoad()

        scrollSegment.setup(buttonCount = 10, buttonMaker = {
            SmartButton(context!!)
        })
        scrollSegment.onSelected = { oldIndex, index ->
            Log.d(javaClass.simpleName, "$oldIndex > $index")
        }
        scrollSegment.select(0, animated = false, needsCallback = false)
    }

    override fun onEnterForeground() {
        super.onEnterForeground()

        scrollSegment.titles = (1..10).map { "ウォッチ$it" }
        scrollSegment.select(0)
    }
}