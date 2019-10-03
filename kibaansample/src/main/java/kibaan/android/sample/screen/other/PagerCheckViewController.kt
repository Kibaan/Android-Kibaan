package kibaan.android.sample.screen.other

import android.view.View
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.*
import kibaan.android.sample.R
import kibaan.android.ui.LoopPagerView

class PagerCheckViewController: SmartViewController() {

    @IBOutlet(R.id.pagerView) lateinit var pagerView: LoopPagerView

    val pageColors = listOf(UIColor.red, UIColor.green, UIColor.blue, UIColor.yellow, UIColor.orange)
    var pageCount = 0

    override fun viewDidLoad() {
        super.viewDidLoad()
    }

    @IBAction(R.id.addPageButton)
    fun actionAddPageButton(@Suppress("UNUSED_PARAMETER") sender: View) {
        val view = View(context)
        view.backgroundColor = pageColors.safeGet(pageCount) ?: UIColor.cyan
        pagerView.addPageView(view)
        pageCount += 1
    }

    @IBAction(R.id.clearPageButton)
    fun actionClearPageButton(@Suppress("UNUSED_PARAMETER") sender: View) {
        pagerView.clear()
        pageCount = 0
    }
}