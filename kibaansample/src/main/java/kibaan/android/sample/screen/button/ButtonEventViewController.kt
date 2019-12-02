package kibaan.android.sample.screen.button

import android.view.View
import kibaan.android.framework.ScreenService
import kibaan.android.framework.SmartViewController
import kibaan.android.ios.IBAction
import kibaan.android.sample.R
import kibaan.android.ui.ToastPopup

/**
 * ボタン確認用画面
 */
class ButtonEventViewController : SmartViewController() {

    @IBAction(R.id.close_button)
    fun actionCloseButton(sender: View) {
        ScreenService.shared.removeSubScreen()
    }

    @IBAction(R.id.button1)
    fun actionButton1(sender: View) {
        ToastPopup.show("Clicked 1", displayTime = 500)
    }

    @IBAction(R.id.button2)
    fun actionButton2(sender: View) {
        ToastPopup.show("Clicked 2", displayTime = 500)
    }

}